package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by dryangkun on 15/3/9.
 */
public class SAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Override
    public void open() {
        super.open();
        sadd();
    }

    @Test
    @Ignore
    public void sadd() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(3), output.getValue());
            }
        };
        aedis.sadd(listener, key, "a".getBytes(), "b".getBytes(), "c".getBytes());
        listener.await();
    }

    @Test public void scard() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(3), output.getValue());
            }
        };
        aedis.scard(listener, key);
        listener.await();
    }

    @Test public void sdiff() {
        _del("xxooabc".getBytes());

        listener = new TestListener<LongOutput>();
        aedis.sadd(listener, "xxooabc".getBytes(), "a".getBytes());
        listener.await();

        listener = new TestListener<SetOutput>() {
            @Override
            protected void doAssert(SetOutput output) {
                assertNotNull(output.getSet());
                Set<BytesKey> set = output.getSet();
                assertEquals(2L, (long) set.size());
                assertTrue(set.contains(BytesKey.createKey("b")));
                assertTrue(set.contains(BytesKey.createKey("c")));
            }
        };
        aedis.sdiff(listener, key, "xxooabc".getBytes());
        listener.await();

        _del("xxooabc".getBytes());
    }

    @Test public void sismember() {
        listener = new TestListener<BooleanOutput>() {
            @Override
            protected void doAssert(BooleanOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.sismember(listener, key, "a".getBytes());
        listener.await();
    }

    @Test public void smembers() {
        listener = new TestListener<SetOutput>() {
            @Override
            protected void doAssert(SetOutput output) {
                assertNotNull(output.getSet());
                Set<BytesKey> set = output.getSet();
                assertEquals(3L, (long) set.size());
            }
        };
        aedis.smembers(listener, key);
        listener.await();
    }

    @Test public void spop() {
        listener = new TestListener<ByteArrayOutput>();
        aedis.spop(listener, key);
        listener.await();
    }

    @Test public void srandmember() {
        listener = new TestListener<ByteArrayOutput>();
        aedis.spop(listener, key);
        listener.await();
    }

    @Test public void srem() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(2), output.getValue());
            }
        };
        aedis.srem(listener, key, "b".getBytes(), "c".getBytes());
        listener.await();

    }

    @Test public void strlen() {
        _del("xxooabc".getBytes());

        listener = new TestListener();
        aedis.set(listener, "xxooabc".getBytes(), "xxoo".getBytes());
        listener.await();

        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(4), output.getValue());
            }
        };
        aedis.strlen(listener, "xxooabc".getBytes());
        listener.await();

        _del("xxooabc".getBytes());
    }

    @Test public void setex() throws Exception {
        listener = new TestListener();
        aedis.setex(listener, key, 3, "abc".getBytes());
        listener.await();

        Thread.sleep(4000L);

        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertNull(output.getValue());
            }
        };
        aedis.get(listener, key);
        listener.await();
    }

    @Test public void setnx() {
        listener = new TestListener();
        aedis.set(listener, key, "abc".getBytes());
        listener.await();

        listener = new TestListener<BooleanOutput>() {
            @Override
            protected void doAssert(BooleanOutput output) {
                assertFalse(output.ok());
            }
        };
        aedis.setnx(listener, key, "xxoo".getBytes());
        listener.await();
    }
}
