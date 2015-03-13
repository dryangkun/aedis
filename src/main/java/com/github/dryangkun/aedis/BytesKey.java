package com.github.dryangkun.aedis;

import io.netty.util.CharsetUtil;

import java.util.Arrays;

/**
 * Created by dryangkun on 15/3/5.
 */
public class BytesKey {

    public final byte[] value;

    public BytesKey(byte[] value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BytesKey) {
            return Arrays.equals(value, ((BytesKey) obj).value);
        }
        return false;
    }

    @Override
    public String toString() {
        return value != null ? new String(value) : "null";
    }

    public static BytesKey createKey(byte[] key) {
        return new BytesKey(key);
    }

    public static BytesKey createKey(String key) {
        return new BytesKey(key.getBytes(CharsetUtil.US_ASCII));
    }
}
