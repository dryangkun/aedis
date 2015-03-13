package com.github.dryangkun.aedis.protocol;

import com.github.dryangkun.aedis.protocol.output.Output;
import com.github.dryangkun.aedis.protocol.output.Result;
import com.github.dryangkun.aedis.protocol.output.ResultType;
import io.netty.buffer.ByteBuf;

/**
 * Created by dryangkun on 15/2/27.
 */
public class CommandParser {
    private Result result;

    public int parse(ByteBuf buf, Output output) {
        if (result == null) {
            result = getResult(buf.readByte());
        }
        buf.markReaderIndex();

        byte[] value;
        int length;
        switch (result.type) {
            case SINGLE:
            case ERROR:
            case INTEGER:
                if ((value = readLine(buf)) != null) {
                    result.setValue(value);
                }
                break;
            case BULK:
                length = readLength(buf);
                if (length != -2) {
                    if (length < 0) {
                        result.setValue(null);
                    }
                    else {
                        if (buf.readableBytes() >= length + 2) {
                            value = Result.EMPTY;
                            if (length > 0) {
                                value = new byte[length];
                                buf.readBytes(value);
                            }
                            buf.readByte();
                            buf.readByte();
                            result.setValue(value);
                        }
                    }
                }
                break;
            case ARRAY:
                length = readLength(buf);
                if (length != -2) {
                    result.setSize(length);
                }
                break;
        }
        if (!result.finished()) {
            buf.resetReaderIndex();
            return 0;
        }
        output.add(result);
        result = null;
        return output.finished() ? 1 : 2;
    }

    private Result getResult(byte b) {
        switch (b) {
            case '+':
                return new Result(ResultType.SINGLE);
            case '-':
                return new Result(ResultType.ERROR);
            case ':':
                return new Result(ResultType.INTEGER);
            case '$':
                return new Result(ResultType.BULK);
            case '*':
                return new Result(ResultType.ARRAY);
            default:
                throw new RuntimeException();
        }
    }

    private byte[] readLine(ByteBuf buf) {
        int i = indexOfCRLF(buf);
        int start = buf.readerIndex();
        if (i < 0) {
            return null;
        }

        byte[] data = Result.EMPTY;
        if (i > start) {
            data = new byte[i - start];
            buf.readBytes(data, 0, data.length);
        }
        buf.readByte();
        buf.readByte();
        return data;
    }

    private int readLength(ByteBuf buf) {
        int len = 0;
        boolean negative = false;
        byte b;

        if (buf.isReadable()) {
            if ((b = buf.readByte()) == '-') {
                negative = true;
                if (buf.isReadable()) {
                    b = buf.readByte();
                }
                else {
                    return -2;
                }
            }
        }
        else {
            return -2;
        }

        while (b != '\r') {
            len = (len * 10) + (b - '0');
            if (!buf.isReadable()) {
                return -2;
            }
            else {
                b = buf.readByte();
            }
        }
        if (!buf.isReadable() || buf.readByte() != '\n') {
            return -2;
        }

        return negative ? -len : len;
    }

    private int indexOfCRLF(ByteBuf buf) {
        int index = -1;
        int start = buf.readerIndex();
        int stop = buf.writerIndex();
        while (start < stop) {
            int i = buf.indexOf(start, stop, (byte)'\r');

            if (i < 0) {
                break;
            }
            else if (i == stop - 1) {
                break;
            }
            else if (buf.getByte(i + 1) == (byte)'\n') {
                index = i;
                break;
            }
            else {
                start = i + 1;
            }
        }
        return index;
    }
}
