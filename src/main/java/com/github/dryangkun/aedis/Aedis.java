package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;
import com.github.dryangkun.aedis.protocol.CommandHandler;
import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.CommandType;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
            if (!closed()) {
                channel.eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        LOG.info("reconnecting - " + channel);
                        reconnect();
                        LOG.info("reconnected - " + channel);
                    }
                }, options.getReconnect_interval_ms(), TimeUnit.MILLISECONDS);
            }
        }
    }

    private final Bootstrap bootstrap;
    private final AedisBootstrap.Options options;

    private volatile boolean closed = false;
    private volatile  Channel channel;
    private CountDownLatch connectLatch;

    public Aedis(Bootstrap bootstrap, final AedisBootstrap.Options options) {
        this.bootstrap = bootstrap;
        this.options = options;
        final int capacity = options.getCommand_queue_capacity();

        this.bootstrap.remoteAddress(options.getHost(), options.getPort())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, options.getConnect_timeout_ms())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(Aedis.this);
                        pipeline.addLast("handler", new CommandHandler(capacity));
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
                    LOG.error("connect fail - " + e.getMessage());
                } finally {
                    connectLatch = null;
                }
            }
        }
    }

    private void reconnect() {
        if (!closed()) synchronized (this) {
            if (!closed()) {
                connectLatch = null;
                ChannelFuture future = bootstrap.connect();
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            Throwable e = future.cause();
                            LOG.error("reconnect fail - " + e.getMessage());
                        }
                    }
                });
                future.channel().closeFuture().addListener(new Reconnect());
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
        final Channel channel = this.channel;
        if (channel != null && channel.isActive()) {
            boolean flushed = false;
            ChannelPipeline pipeline = channel.pipeline();
            CommandHandler handler = (CommandHandler) pipeline.get("handler");

            for (Command command : commands) {
                if (handler.write(command, options.getTimeout_ms())) {
                    flushed = true;
                }
            }
            if (flushed) {
                channel.eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        for (Command command : commands) {
                            command.tryTimeout();
                        }
                    }
                }, options.getTimeout_ms(), TimeUnit.MILLISECONDS);
                handler.trigger(pipeline);
            }
        }
        else {
            for (Command command : commands) {
                command.tryInactive();
            }
        }
    }

    @Override
    public void dispatch(final Command command) {
        final Channel channel = this.channel;
        if (channel != null && channel.isActive()) {
            dispatch0(command, channel, options.getTimeout_ms());
        }
        else {
            command.tryInactive();
        }
    }

    private void dispatch0(final Command command, final Channel channel, int timeout_ms) {
        ChannelPipeline pipeline = channel.pipeline();
        CommandHandler handler = (CommandHandler) pipeline.get("handler");
        if (handler.write(command, timeout_ms)) {
            command.setTimeoutTask(channel.eventLoop(), timeout_ms);
            handler.trigger(pipeline);
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
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        String auth = options.getAuth();
        final Channel channel = ctx.channel();
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

                    if (success) {
                        Aedis.this.channel = channel;
                    }
                    else {
                        channel.close();
                    }
                    if (connectLatch != null) connectLatch.countDown();
                }
            }, new StatusOutput(), auth.getBytes());
            dispatch0(command, channel, options.getConnect_timeout_ms());
        }
        else {
            this.channel = channel;
            if (connectLatch != null) connectLatch.countDown();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channel = null;
        ctx.fireChannelInactive();
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
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }
}
