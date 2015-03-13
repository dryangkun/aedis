package com.github.dryangkun.aedis.protocol.output;

/**
 * Created by dryangkun on 15/3/9.
 */
public class DoubleOutput extends Output {

    private Double value = null;

    @Override
    protected void init0(Result result) {
        byte[] value = result.value;
        if (value != null && value.length > 0) {
            try {
                this.value = Double.parseDouble(new String(value));
            } catch (NumberFormatException e) {

            }
        }
        finished = true;
    }

    @Override
    protected void add0(Result result) {

    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }
}
