package com.github.dryangkun.aedis.protocol.output;

/**
 * Created by dryangkun on 15/3/5.
 */
public class LongOutput extends Output {

    private Long value = null;

    @Override
    protected void init0(Result result) {
        byte[] value = result.value;
        if (value != null && value.length > 0) {
            this.value = parseLong(value);
        }
        finished = true;
    }

    @Override
    protected void add0(Result result) {

    }

    public Long getValue() {
        return value;
    }

    private static Long parseLong(byte[] value) {
        long ret = 0L;
        int i = 0;
        boolean negative = false;

        if (value[i] == '-') {
            if (value.length > 1) {
                negative = true;
                i++;
            }
            else {
                return null;
            }
        }
        for (; i < value.length; i++) {
            ret = (ret * 10) + (value[i] - '0');
        }
        return negative ? -ret : ret;
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }
}
