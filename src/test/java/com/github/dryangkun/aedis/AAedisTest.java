package com.github.dryangkun.aedis;

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
public class AAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void append() {
        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.set(listener, key, "123".getBytes());
        listener.await();

        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(6), output.getValue());
            }
        };
        aedis.append(listener, key, "123".getBytes());
        listener.await();
    }
}
