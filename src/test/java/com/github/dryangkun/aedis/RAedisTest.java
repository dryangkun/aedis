package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.ByteArrayOutput;
import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by dryangkun on 15/3/9.
 */
public class RAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Override
    public void open() {
        super.open();

        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(3), output.getValue());
            }
        };
        aedis.rpush(listener, key, "a".getBytes(), "b".getBytes(), "c".getBytes());
        listener.await();
    }

    @Test public void randomkey() {
        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertNotNull(output.getValue());
            }
        };
        aedis.randomkey(listener);
        listener.await();
    }

    @Test public void rename() {
        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.set(listener, key, "abc".getBytes());
        listener.await();

        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.rename(listener, key, "xxoo123".getBytes());
        listener.await();
    }

    @Test public void rpop() {
        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertArrayEquals("c".getBytes(), output.getValue());
            }
        };
        aedis.rpop(listener, key);
        listener.await();
    }

    @Test public void rpoplpush() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {

            }
        };
        aedis.del(listener, "xxoo123".getBytes());
        listener.await();

        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertArrayEquals("c".getBytes(), output.getValue());
            }
        };
        aedis.rpoplpush(listener, key, "xxoo123".getBytes());
        listener.await();

        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(1), output.getValue());
            }
        };
        aedis.llen(listener, "xxoo123".getBytes());
        listener.await();

        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {

            }
        };
        aedis.del(listener, "xxoo123".getBytes());
        listener.await();
    }


}
