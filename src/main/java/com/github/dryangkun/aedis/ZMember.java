package com.github.dryangkun.aedis;

import java.util.Arrays;

/**
 * Created by dryangkun on 15/3/9.
 */
public class ZMember implements Comparable<ZMember> {

    public final double score;
    public final byte[] member;

    public ZMember(double score, byte[] member) {
        this.score = score;
        this.member = member;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(member);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ZMember) {
            return Arrays.equals(member, ((ZMember) obj).member);
        }
        return false;
    }

    @Override
    public int compareTo(ZMember o) {
        return Double.compare(score, o.score);
    }

    @Override
    public String toString() {
        return "(score=" + score + ",member=" + (member != null ? new String(member) : "null") + ")";
    }
}
