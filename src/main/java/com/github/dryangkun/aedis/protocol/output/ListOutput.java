package com.github.dryangkun.aedis.protocol.output;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dryangkun on 15/3/4.
 */
public class ListOutput extends ArrayOutput {

    private List<byte[]> list;

    @Override
    protected void init0(Result result) {
        super.init0(result);
        if (size > 0) {
            list = new ArrayList<byte[]>(size);
        }
    }

    @Override
    protected void add1(Result result) {
        list.add(result.value);
    }

    public List<byte[]> getList() {
        return list;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("list[");
        if (list != null)
            for (byte[] bytes : list) {
                builder.append(toString(bytes));
                builder.append(",");
            }
        builder.append("]");
        return builder.toString();
    }
}
