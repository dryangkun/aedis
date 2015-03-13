package com.github.dryangkun.aedis.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by dryangkun on 15/3/13.
 */
@ChannelHandler.Sharable
public class CommandEncoder extends MessageToByteEncoder<Command> {

    public static final CommandEncoder INSTANCE = new CommandEncoder();

    @Override
    protected void encode(ChannelHandlerContext ctx, Command command, ByteBuf out) throws Exception {
        command.encode(out);
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, Command command, boolean preferDirect) throws Exception {
        if (command.isDone()) {
            return Unpooled.EMPTY_BUFFER;
        }
        if (!ctx.channel().isActive()) {
            command.tryInactive();
            return Unpooled.EMPTY_BUFFER;
        }
        if (!command.offerCommand()) {
            command.tryQueuefull();
            return Unpooled.EMPTY_BUFFER;
        }

        if (preferDirect) {
            return ctx.alloc().ioBuffer(command.getByteBufCapacity());
        } else {
            return ctx.alloc().heapBuffer(command.getByteBufCapacity());
        }
    }
}
