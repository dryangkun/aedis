package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dryangkun on 15/3/9.
 */
public class EAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void echo() {
        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertArrayEquals("123".getBytes(), output.getValue());
            }
        };
        aedis.echo(listener, "123".getBytes());
        listener.await();
    }

    @Test
    public void exists() {
        listener =  new TestListener<BooleanOutput>();
        aedis.exists(listener, key);
        listener.await();
    }

    @Test
    public void expire() {
        listener = new TestListener<BooleanOutput>();
        aedis.expire(listener, key, 86400);
        listener.await();
    }
}
