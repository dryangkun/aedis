package com.github.dryangkun.aedis.protocol;

import com.github.dryangkun.aedis.Charsets;
import com.github.dryangkun.aedis.protocol.output.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by dryangkun on 15/3/1.
 */
public class Command<A extends Output> {

    private static final InternalLogger LOG = InternalLoggerFactory.getInstance(Command.class);

    private static class CauseHodler {
        private final Throwable cause;

        public CauseHodler(Throwable cause) {
            this.cause = cause;
        }
    }

    private static final Throwable TIMEOUT = new Throwable("timeout");
    private static final Throwable INACTIVE = new Throwable("inactive");
    private static final Throwable QUEUEFULL = new Throwable("queuefull");

    private static final byte[] CRLF = "\r\n".getBytes(Charsets.ASCII);

    private final CommandType type;
    private final CommandListener<A> listener;
    private final A output;

    private List<byte[]> argsList;
    private int argsByteSize;

    private volatile Object result = null;

    public Command() {
        type = null;
        listener = null;
        output = null;
    }

    public Command(CommandType type, CommandListener<A> listener, A output, byte[]... args) {
        this(type, listener, output, args.length);
        for (byte[] bytes : args) {
            argsList.add(bytes);
            argsByteSize += getIntStrLength(bytes.length) + bytes.length;
        }
    }

    public Command(CommandType type, CommandListener<A> listener, A output, int argsSize) {
        this.type = type;
        this.listener = listener;
        this.output = output;
        argsList = new ArrayList<byte[]>(argsSize + 1);
        argsList.add(type.bytes);
        argsByteSize = getIntStrLength(type.bytes.length) + type.bytes.length;
    }

    public Command<A> addArg(byte[] arg) {
        argsList.add(arg);
        argsByteSize += getIntStrLength(arg.length) + arg.length;
        return this;
    }

    public Command<A> addArg(CommandKeyword arg) {
        return addArg(arg.bytes);
    }

    public Command<A> addArg(String arg) {
        return addArg(arg.getBytes(Charsets.ASCII));
    }

    public Command<A> addArg(long arg) {
        return addArg(Long.toString(arg).getBytes());
    }

    public Command<A> addArg(int arg) {
        return addArg(Integer.toString(arg).getBytes());
    }

    public Command<A> addArg(double arg) {
        String value;
        if (Double.isInfinite(arg)) {
            value = (arg > 0) ? "+inf" : "-inf";
        }
        else {
            value = Double.toString(arg);
        }
        return addArg(value);
    }

    public Command<A> addArgs(byte[]... args) {
        for (byte[] bytes : args) {
            argsList.add(bytes);
            argsByteSize += getIntStrLength(bytes.length) + bytes.length;
        }
        return this;
    }

    public void encode(ByteBuf buf) {
        buf.writeByte('*');
        writeIntStr(buf, argsList.size());
        buf.writeBytes(CRLF);
        for (byte[] bytes : argsList) {
            buf.writeByte('$');
            writeIntStr(buf, bytes.length);
            buf.writeBytes(CRLF);
            buf.writeBytes(bytes);
            buf.writeBytes(CRLF);
        }
        argsList = null;
    }

    private static int getIntStrLength(int value) {
        return ("" + value).length();
    }

    private static void writeIntStr(ByteBuf buf, int value) {
        if (value < 10) {
            buf.writeByte('0' + value);
        }
        else {
            buf.writeBytes(("" + value).getBytes());
        }
    }

    public int getByteBufCapacity() {
        return 1 + getIntStrLength(argsList.size()) + 2 + argsByteSize + 5 * argsList.size();
    }

    public A get() {
        return output;
    }

    public boolean tryTimeout() {
        return tryFailure(TIMEOUT);
    }

    public boolean tryInactive() {
        return tryFailure(INACTIVE);
    }

    public boolean tryQueuefull() {
        return tryFailure(QUEUEFULL);
    }

    private boolean tryFailure(Throwable cause) {
        if (doTry(cause)) {
            listener.operationComplete(this);
            return true;
        }
        return false;
    }

    public boolean tryOutput() {
        Object result = output.cause() == null ? output : new CauseHodler(output.cause());
        if (doTry(result)) {
            listener.operationComplete(this);
            return true;
        }
        return false;
    }

    private boolean doTry(Object result) {
        if (this.result == null) {
            synchronized (this) {
                if (this.result == null) {
                    argsList = null;
                    this.result = result;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDone() {
        return result != null;
    }

    public boolean isOutputSuccess() {
        return result instanceof Output;
    }

    public boolean isOutputFailure() {
        return result instanceof CauseHodler;
    }

    public boolean isTimeoutFailure() {
        return result == TIMEOUT;
    }

    public boolean isInactiveFailure() {
        return result == INACTIVE;
    }

    public boolean isQueuefullFailure() {
        return result == QUEUEFULL;
    }

    public Throwable cause() {
        if (result instanceof Throwable) {
            return (Throwable) result;
        }
        else if (result instanceof CauseHodler) {
            return ((CauseHodler) result).cause;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("command-" + type.name() + ", ");
        if (!isDone()) {
            builder.append("result-not done");
        }
        else {
            if (isOutputSuccess()) {
                builder.append("result-" + get().toString());
            }
            else {
                builder.append("cause-" + cause());
            }
        }
        return builder.toString();
    }
}
