package com.github.dryangkun.aedis.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dryangkun on 15/3/1.
 */
public class CommandHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger LOG = InternalLoggerFactory.getInstance(CommandHandler.class);
    private static final Object EVENT = new Object();

    private final LinkedBlockingQueue<Command> readQueue;
    private final CommandParser parser = new CommandParser();
    private ByteBuf buffer;

    private final LinkedBlockingQueue<Command> writeQueue;
    private final AtomicBoolean triggered = new AtomicBoolean(false);

    public CommandHandler(int capacity) {
        readQueue = new LinkedBlockingQueue<Command>(capacity);
        writeQueue = new LinkedBlockingQueue<Command>(capacity);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Command command;
        {
            while ((command = writeQueue.poll()) != null) {
                command.tryInactive();
            }
        }
        {
            while ((command = readQueue.poll()) != null) {
                command.tryInactive();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (buffer != null)
            buffer.release();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if (LOG.isDebugEnabled()) {
            LOG.debug("read - " + buf.toString(CharsetUtil.US_ASCII));
        }
        if (!buf.isReadable()) {
            return;
        }
        if (buffer == null) {
            buffer = ctx.alloc().buffer(buf.readableBytes());
        }
        buffer.writeBytes(buf);
        buf.release();

        while (buffer.isReadable()) {
            Command command = readQueue.peek();
            int state = parser.parse(buffer, command.get());
            if (state == 0) {
                break;
            }
            else if (state == 1) {
                command.tryOutput();
                readQueue.poll();
            }
        }
        if (buffer.isReadable()) {
            buffer.discardReadBytes();
        }
        else {
            buffer.release();
            buffer = null;
        }
    }

    public boolean write(final Command command, int timeout_ms) {
        boolean success = false;
        try {
            success = writeQueue.offer(command, timeout_ms, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            //ignore
        }
        if (!success) {
            command.tryQueuefull();
        }
        return success;
    }

    public void trigger(ChannelPipeline pipeline) {
        if (triggered.compareAndSet(false, true)) {
            pipeline.fireUserEventTriggered(EVENT);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        triggered.set(false);
        Channel channel = ctx.channel();
        Command command;
        if (channel.isActive()) {
            send(channel, ctx);
        }
        else {
            while ((command = writeQueue.poll()) != null) {
                command.tryInactive();
            }
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            send(channel, ctx);
        }
    }

    private void send(Channel channel, ChannelHandlerContext ctx) {
        Command command;
        boolean flushed = false;
        while (channel.isWritable() && (command = writeQueue.poll()) != null) {
            if (command.isDone())
                continue;
            if (readQueue.offer(command)) {
                ByteBuf buf = channel.alloc().buffer(command.getByteBufCapacity());
                command.encode(buf);
                ctx.write(buf, channel.voidPromise());
                flushed = true;
            }
            else {
                command.tryQueuefull();
            }
        }
        if (flushed)
            ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("exceptionCaught - " + cause.getMessage(), cause);
        ctx.close();
    }
}
