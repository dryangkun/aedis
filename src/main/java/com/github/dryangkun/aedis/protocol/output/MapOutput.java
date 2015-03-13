package com.github.dryangkun.aedis.protocol.output;

import com.github.dryangkun.aedis.BytesKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dryangkun on 15/3/4.
 */
public class MapOutput extends ArrayOutput {

    private Map<BytesKey, byte[]> map;
    private BytesKey key = null;

    @Override
    protected void init0(Result result) {
        super.init0(result);
        if (size > 0) {
            map = new HashMap<BytesKey, byte[]>(size / 2);
        }
    }

    @Override
    protected void add1(Result result) {
        if (key == null) {
            key = BytesKey.createKey(result.value);
        }
        else {
            map.put(key, result.value);
            key = null;
        }
    }

    public Map<BytesKey, byte[]> getMap() {
        return map;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("map{");
        if (map != null)
            for (BytesKey k : map.keySet()) {
                builder.append(k.toString() + "=" + toString(map.get(k)));
                builder.append(",");
            }
        builder.append("}");
        return builder.toString();
    }
}
