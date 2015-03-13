package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.DoubleOutput;
import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dryangkun on 15/3/9.
 */
public class IAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test public void incr() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(1), output.getValue());
            }
        };
        aedis.incr(listener, key);
        listener.await();
    }

    @Test public void incrby() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(10), output.getValue());
            }
        };
        aedis.incrby(listener, key, 10);
        listener.await();
    }

    @Test public void incrbyfloat() {
        listener = new TestListener<DoubleOutput>() {
            @Override
            protected void doAssert(DoubleOutput output) {
                assertEquals(new Double(1.5), output.getValue());
            }
        };
        aedis.incrbyfloat(listener, key, 1.5);
        listener.await();
    }
}
