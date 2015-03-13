package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by dryangkun on 15/3/9.
 */
public class HAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Override
    @Before
    public void open() {
        super.open();

        listener = new TestListener<BooleanOutput>() {
            @Override
            protected void doAssert(BooleanOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.hset(listener, key, "a".getBytes(), "1".getBytes());
        listener.await();
    }

    @Test
    public void hsetnx() {
        listener = new TestListener<BooleanOutput>() {
            @Override
            protected void doAssert(BooleanOutput output) {
                assertFalse(output.ok());
            }
        };
        aedis.hsetnx(listener, key, "a".getBytes(), "b".getBytes());
        listener.await();
    }

    @Test
    public void hdel() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(1), output.getValue());
            }
        };
        aedis.hdel(listener, key, "a".getBytes());
        listener.await();
    }

    @Test
    public void hexists() {
        listener = new TestListener<BooleanOutput>() {
            @Override
            protected void doAssert(BooleanOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.hexists(listener, key, "a".getBytes());
        listener.await();
    }

    @Test
    public void hget() {
        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertArrayEquals("1".getBytes(), output.getValue());
            }
        };
        aedis.hget(listener, key, "a".getBytes());
        listener.await();
    }

    @Test
    public void hincrby() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(11), output.getValue());
            }
        };
        aedis.hincrby(listener, key, "a".getBytes(), 10);
        listener.await();
    }

    @Test
    public void hgetAll() {
        listener = new TestListener<MapOutput>() {
            @Override
            protected void doAssert(MapOutput output) {
                assertNotNull(output.getMap());

                Map<BytesKey, byte[]> map = output.getMap();
                BytesKey k = BytesKey.createKey("a".getBytes());
                assertArrayEquals("1".getBytes(), map.get(k));
            }
        };
        aedis.hgetAll(listener, key);
        listener.await();
    }

    @Test public void hkeys() {
        listener = new TestListener<ListOutput>() {
            @Override
            protected void doAssert(ListOutput output) {
                assertNotNull(output.getList());
                List<byte[]> list = output.getList();
                assertArrayEquals("a".getBytes(), list.get(0));
            }
        };
        aedis.hkeys(listener, key);
        listener.await();
    }

    @Test public void hlen() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(1), output.getValue());
            }
        };
        aedis.hlen(listener, key);
        listener.await();
    }

    @Test public void hmset() {
        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        Map<BytesKey, byte[]> map = new HashMap<BytesKey, byte[]>();
        map.put(BytesKey.createKey("b"), "b".getBytes());
        map.put(BytesKey.createKey("c"), "c".getBytes());
        aedis.hmset(listener, key, map);
        listener.await();
    }

    @Test public void hvals() {
        listener = new TestListener<ListOutput>() {
            @Override
            protected void doAssert(ListOutput output) {
                assertNotNull(output.getList());
                List<byte[]> list = output.getList();
                assertArrayEquals("1".getBytes(), list.get(0));
            }
        };
        aedis.hvals(listener, key);
        listener.await();
    }
}
