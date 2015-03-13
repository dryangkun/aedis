package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.DoubleOutput;
import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import com.github.dryangkun.aedis.protocol.output.ZMemberListOutput;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by dryangkun on 15/3/9.
 */
public class ZAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Override
    public void open() {
        super.open();
        zadd();
    }

    @Test
    @Ignore
    public void zadd() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(2), output.getValue());
            }
        };
        aedis.zadd(listener, key, new ZMember(0.5, "x".getBytes()), new ZMember(0.6, "y".getBytes()));
        listener.await();
    }

    @Test public void zcard() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(2), output.getValue());
            }
        };
        aedis.zcard(listener, key);
        listener.await();
    }

    @Test public void zcount() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(1), output.getValue());
            }
        };
        aedis.zcount(listener, key, 0.5, 0.55);
        listener.await();
    }

    @Test public void zrangeWithScores() {
        listener = new TestListener<ZMemberListOutput>() {
            @Override
            protected void doAssert(ZMemberListOutput output) {
                assertNotNull(output.getList());
                List<ZMember> list = output.getList();
                assertEquals(new ZMember(0.5, "x".getBytes()), list.get(0));
            }
        };
        aedis.zrangeWithScores(listener, key, 0, 0);
        listener.await();
    }

    @Test public void zrangebyscoreWithScores() {
        listener = new TestListener<ZMemberListOutput>() {
            @Override
            protected void doAssert(ZMemberListOutput output) {
                assertNotNull(output.getList());
                List<ZMember> list = output.getList();
                assertEquals(new ZMember(0.5, "x".getBytes()), list.get(0));
            }
        };
        aedis.zrangebyscoreWithScores(listener, key, 0.5, 0.55);
        listener.await();
    }

    @Test public void zrank() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(1), output.getValue());
            }
        };
        aedis.zrank(listener, key, "y".getBytes());
        listener.await();
    }

    @Test public void zrevrank() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(0), output.getValue());
            }
        };
        aedis.zrevrank(listener, key, "y".getBytes());
        listener.await();
    }

    @Test public void zscore() {
        listener = new TestListener<DoubleOutput>() {
            @Override
            protected void doAssert(DoubleOutput output) {
                assertEquals(new Double(0.5), output.getValue());
            }
        };
        aedis.zscore(listener, key, "x".getBytes());
        listener.await();
    }
}
