package com.github.dryangkun.aedis.protocol.output;

/**
 * Created by dryangkun on 15/3/4.
 */
public class ByteArrayOutput extends Output {

    private byte[] value;

    @Override
    protected void init0(Result result) {
        value = result.value;
        finished = true;
    }

    @Override
    protected void add0(Result result) {

    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return toString(value);
    }
}
