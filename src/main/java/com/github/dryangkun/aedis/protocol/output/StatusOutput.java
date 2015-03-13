package com.github.dryangkun.aedis.protocol.output;

/**
 * Created by dryangkun on 15/3/4.
 */
public class StatusOutput extends Output {

    private boolean ok = false;

    @Override
    protected void init0(Result result) {
        byte[] value = result.value;
        if (value != null && value.length == 2) {
            if (value[0] == 'O' && value[1] == 'K') {
                ok = true;
            }
        }
        finished = true;
    }

    @Override
    protected void add0(Result result) {

    }

    public boolean ok() {
        return ok;
    }

    @Override
    public String toString() {
        return ok ? "OK" : "NOT OK";
    }
}
