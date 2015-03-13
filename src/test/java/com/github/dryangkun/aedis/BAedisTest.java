package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.Output;
import com.github.dryangkun.aedis.protocol.output.SingleOutput;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dryangkun on 15/3/9.
 */
public class BAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void bgrewriteaof() {
        listener = new TestListener<SingleOutput>() {
            @Override
            protected void doAssert(SingleOutput output) {

            }
        };
        aedis.bgrewriteaof(listener);
        listener.await();
    }

    @Test
    public void bgsave() {
        listener = new TestListener<SingleOutput>() {
            @Override
            protected void doAssert(SingleOutput output) {

            }
        };
        aedis.bgsave(listener);
        listener.await();
    }

    @Test
    public void bitcount() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {

            }
        };
        aedis.setbit(listener, key, 7, 1);
        listener.await();

        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(1), output.getValue());
            }
        };
        aedis.bitcount(listener, key);
        listener.await();
    }
}
