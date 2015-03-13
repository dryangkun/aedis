package com.github.dryangkun.aedis.protocol.output;

/**
 * Created by dryangkun on 15/3/4.
 */
public abstract class Output {

    private boolean inited = false;
    protected boolean finished = false;
    private Throwable cause;

    public void add(Result result) {
        if (!inited) {
            inited = true;
            if (result.type == ResultType.ERROR) {
                cause = new Throwable(new String(result.value));
                finished = true;
            }
            else {
                init0(result);
            }
        }
        else {
            add0(result);
        }
    }

    protected abstract void init0(Result result);

    protected abstract void add0(Result result);

    public Throwable cause() {
        return cause;
    }

    public boolean finished() {
        return finished;
    }

    public abstract String toString();

    protected static String toString(byte[] value) {
        return value != null ? new String(value) : "null";
    }
}
