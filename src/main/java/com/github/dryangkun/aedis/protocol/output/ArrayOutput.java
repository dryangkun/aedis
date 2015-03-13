package com.github.dryangkun.aedis.protocol.output;

/**
 * Created by dryangkun on 15/3/4.
 */
public abstract class ArrayOutput extends Output {

    protected int size;

    @Override
    protected void init0(Result result) {
        size = result.size;
        if (size == 0) {
            finished = true;
        }
    }

    @Override
    protected void add0(Result result) {
        add1(result);
        if (--size == 0) {
            finished = true;
        }
    }

    protected abstract void add1(Result result);
}
