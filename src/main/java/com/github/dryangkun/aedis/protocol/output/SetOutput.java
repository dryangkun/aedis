package com.github.dryangkun.aedis.protocol.output;

import com.github.dryangkun.aedis.BytesKey;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dryangkun on 15/3/9.
 */
public class SetOutput extends ArrayOutput {

    private Set<BytesKey> set;

    @Override
    protected void init0(Result result) {
        super.init0(result);
        if (size > 0) {
            set = new HashSet<BytesKey>(size);
        }
    }

    @Override
    protected void add1(Result result) {
        set.add(BytesKey.createKey(result.value));
    }

    public Set<BytesKey> getSet() {
        return set;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("set<");
        if (set != null)
            for (BytesKey k : set) {
                builder.append(k.toString() + ",");
            }
        builder.append(">");
        return builder.toString();
    }
}
