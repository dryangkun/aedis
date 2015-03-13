package com.github.dryangkun.aedis.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by dryangkun on 15/3/1.
 */
public class CommandReceiver extends ChannelInboundHandlerAdapter {

    private static final InternalLogger LOG = InternalLoggerFactory.getInstance(CommandReceiver.class);

    private final LinkedBlockingQueue<Command> queue;
    private final CommandParser parser = new CommandParser();
    private ByteBuf buffer;

    public CommandReceiver(LinkedBlockingQueue<Command> queue) {
        this.queue = queue;
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
            buffer = buf;
        }
        else {
            buffer.writeBytes(buf);
            buf.release();
        }

        while (buffer.isReadable()) {
            Command command = queue.peek();
            int state = parser.parse(buffer, command.get());
            if (state == 0) {
                break;
            }
            else if (state == 1) {
                command.tryOutput();
                queue.poll();
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
}
