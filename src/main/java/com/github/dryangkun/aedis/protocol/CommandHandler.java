package com.github.dryangkun.aedis.protocol;

import com.github.dryangkun.aedis.Charsets;
import com.github.dryangkun.aedis.protocol.output.Output;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by dryangkun on 15/3/1.
 */
public class CommandHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger LOG = InternalLoggerFactory.getInstance(CommandHandler.class);

    private static class Count {
        public int count;
        public Count(int count) {
            this.count = count;
        }
    }

    private final LinkedBlockingQueue<Command> queue;
    private final CommandParser parser = new CommandParser();
    private ByteBuf buffer;

    public CommandHandler(LinkedBlockingQueue<Command> queue) {
        this.queue = queue;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if (LOG.isDebugEnabled()) {
            LOG.debug("read - " + buf.toString(Charsets.ASCII));
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
