package com.github.dryangkun.aedis.protocol.output;

/**
 * Created by dryangkun on 15/3/4.
 */
public class Result {

    public static final byte[] EMPTY = new byte[0];

    public final ResultType type;

    private boolean finished = false;
    public byte[] value;
    public int size;

    public Result(ResultType type) {
        this.type = type;
    }

    public void setValue(byte[] value) {
        this.value = value;
        finished = true;
    }

    public void setSize(int size) {
        this.size = size;
        finished = true;
    }

    public boolean finished() {
        return finished;
    }

    public boolean isArray() {
        return type == ResultType.ARRAY;
    }
}
