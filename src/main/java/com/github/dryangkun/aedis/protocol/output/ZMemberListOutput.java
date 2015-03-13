package com.github.dryangkun.aedis.protocol.output;

import com.github.dryangkun.aedis.ZMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dryangkun on 15/3/9.
 */
public class ZMemberListOutput extends ArrayOutput {

    private List<ZMember> list;
    private byte[] member;
    private boolean isMember = true;

    @Override
    protected void init0(Result result) {
        super.init0(result);
        if (size > 0) {
            list = new ArrayList<ZMember>(size / 2);
        }
    }

    @Override
    protected void add1(Result result) {
        if (isMember) {
            member = result.value;
        }
        else {
            double score = 0;
            if (result.value != null && result.value.length > 0) {
                try {
                    score = Double.parseDouble(new String(result.value));
                } catch (NumberFormatException e) {

                }
            }
            list.add(new ZMember(score, member));
            member = null;
        }
        isMember = !isMember;
    }

    public List<ZMember> getList() {
        return list;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("zset<");
        if (list != null)
            for (ZMember member : list) {
                builder.append(member.toString() + ",");
            }
        builder.append(">");
        return builder.toString();
    }
}
