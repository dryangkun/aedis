package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.*;
import com.github.dryangkun.aedis.protocol.output.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dryangkun on 15/3/1.
 */
@ChannelHandler.Sharable
public class Aedis extends AedisBase implements IClosable, IPipeline, ChannelInboundHandler {

    private static final InternalLogger LOG = InternalLoggerFactory.getInstance(Aedis.class);

    private class Reconnect implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            final Channel channel = future.channel();
            if (!closed()) channel.eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                    LOG.debug("reconnect - " + channel);
                    connect();
                }
            }, options.getReconnect_interval_ms(), TimeUnit.MILLISECONDS);
        }
    }

    private final Bootstrap bootstrap;
    private final AedisBootstrap.Options options;
    private final LinkedBlockingQueue<Command> queue;
    private final Lock wlock = new ReentrantLock();

    private volatile boolean closed = false;
    private volatile  Channel channel;
    private CountDownLatch connectLatch;

    public Aedis(Bootstrap bootstrap, AedisBootstrap.Options options) {
        this.bootstrap = bootstrap;
        this.options = options;
        this.queue = new LinkedBlockingQueue<Command>(options.getCommand_queue_size());

        this.bootstrap.remoteAddress(options.getHost(), options.getPort())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, options.getConnect_timeout_ms())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(Aedis.this, new CommandHandler(queue));
                    }
                });
    }

    public void connect() {
        if (!closed()) synchronized (this) {
            if (!closed()) {
                connectLatch = new CountDownLatch(1);
                ChannelFuture future = bootstrap.connect();
                future.channel().closeFuture().addListener(new Reconnect());
                try {
                    future.awaitUninterruptibly();
                    if (!future.isSuccess()) {
                        throw future.cause();
                    }
                    try {
                        connectLatch.await();
                    } catch (InterruptedException e) {
                        //ignore
                    }
                } catch (Throwable e) {
                    LOG.error("connect fail", e);
                }
            }
        }
    }

    @Override
    public void close() {
        if (!closed()) synchronized (this) {
            if (!closed()) {
                closed = true;
                if (channel != null) {
                    try {
                        channel.close().sync();
                    } catch (InterruptedException e) {
                        //ignore
                    }
                }
            }
        }
    }

    @Override
    public boolean closed() {
        return closed;
    }

    public void dispatch(final List<Command> commands) {
        boolean inactive = true;
        if (channel != null) {
            boolean trylock = false;
            try {
                trylock = wlock.tryLock(options.getTimeout_ms(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //ignore
            }

            if (trylock) {
                if (channel != null && channel.isActive()) {
                    inactive = false;
                    boolean flushed = false;
                    for (Command command : commands) {
                        if (queue.offer(command)) {
                            ByteBuf buf = channel.alloc().buffer(command.getByteBufCapacity());
                            command.encode(buf);
                            channel.writeAndFlush(buf, channel.voidPromise());
                            flushed = true;
                        }
                        else {
                            command.tryQueuefull();
                        }
                    }

                    if (flushed) {
                        channel.flush();
                        channel.eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                for (Command command : commands) {
                                    command.tryTimeout();
                                }
                            }
                        }, options.getTimeout_ms(), TimeUnit.MILLISECONDS);
                    }
                }
                wlock.unlock();
            }
            else {
                for (Command command : commands) {
                    command.tryTimeout();
                }
            }
        }
        if (inactive)
            for (Command command : commands) {
                command.tryInactive();
            }
    }

    @Override
    public void dispatch(final Command command) {
        boolean inactive = true;
        if (channel != null) {
            boolean trylock = false;
            try {
                trylock = wlock.tryLock(options.getTimeout_ms(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //ignore
            }

            if (trylock) {
                if (channel != null && channel.isActive()) {
                    inactive = false;
                    dispatch0(command, channel);
                }
                wlock.unlock();
            }
            else {
                command.tryTimeout();
            }
        }
        if (inactive)
            command.tryInactive();
    }

    private void dispatch0(final Command command, final Channel channel) {
        if (queue.offer(command)) {
            ByteBuf buf = channel.alloc().buffer(command.getByteBufCapacity());
            command.encode(buf);
            channel.writeAndFlush(buf, channel.voidPromise());
            channel.eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                    command.tryTimeout();
                }
            }, options.getTimeout_ms(), TimeUnit.MILLISECONDS);
        }
        else {
            command.tryQueuefull();
        }
    }

    @Override
    public Pipeline pipeline() {
        return new Pipeline(this);
    }

    @Override
    public Pipeline pipeline(int capacity) {
        return new Pipeline(this, capacity);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        wlock.lock();
        Command command;
        while ((command = queue.poll()) != null) {
            command.tryInactive();
        }
        wlock.unlock();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String auth = options.getAuth();
        final Channel channel = ctx.channel();
        wlock.lock();
        if (auth != null && auth.length() > 0) {
            Command<StatusOutput> command = new Command<StatusOutput>(CommandType.AUTH, new CommandListener<StatusOutput>() {
                @Override
                public void operationComplete(Command<StatusOutput> command) {
                    boolean success = false;
                    if (command.isOutputSuccess()) {
                        success = command.get().ok();
                    }
                    else if (command.isOutputFailure()) {
                        String message = command.cause().getMessage();
                        if ("ERR Client sent AUTH, but no password is set".equals(message)) {
                            success = true;
                        }
                    }

                    wlock.lock();
                    if (success) {
                        Aedis.this.channel = channel;
                    }
                    else {
                        channel.close();
                    }
                    connectLatch.countDown();
                    wlock.unlock();
                }
            }, new StatusOutput(), auth.getBytes());
            dispatch0(command, channel);
        }
        else {
            this.channel = channel;
            connectLatch.countDown();
        }
        wlock.unlock();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        wlock.lock();
        channel = null;
        wlock.unlock();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("exceptionCaught", cause);
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }
}
