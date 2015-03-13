package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.ByteArrayOutput;
import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dryangkun on 15/3/9.
 */
public class GAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void get_getset() {
        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.set(listener, key, "123".getBytes());
        listener.await();

        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertArrayEquals("123".getBytes(), output.getValue());
            }
        };
        aedis.getset(listener, key, "456".getBytes());
        listener.await();

        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertArrayEquals("456".getBytes(), output.getValue());
            }
        };
        aedis.get(listener, key);
        listener.await();
    }

    @Test
    public void getbit() {
        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.set(listener, key, new byte[] {31});
        listener.await();

        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(1), output.getValue());
            }
        };
        aedis.getbit(listener, key, 3);
        listener.await();
    }
}
