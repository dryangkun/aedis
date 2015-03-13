package com.github.dryangkun.aedis.protocol.output;

/**
 * Created by dryangkun on 15/3/9.
 */
public class BooleanOutput extends LongOutput {

    public boolean ok() {
        return getValue() == 1;
    }

    @Override
    public String toString() {
        return ok() ? "TRUE" : "FALSE";
    }
}
