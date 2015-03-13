package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.ListOutput;
import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
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
public class MAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test public void mset_mget() {
        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        Map<BytesKey, byte[]> map = new HashMap<BytesKey, byte[]>();
        map.put(BytesKey.createKey(key), "abc".getBytes());
        aedis.mset(listener, map);
        listener.await();

        listener = new TestListener<ListOutput>() {
            @Override
            protected void doAssert(ListOutput output) {
                assertNotNull(output.getList());
                List<byte[]> list = output.getList();
                assertArrayEquals("abc".getBytes(), list.get(0));
            }
        };
        aedis.mget(listener, key);
        listener.await();
    }

    @Test public void msetnx() {
        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        Map<BytesKey, byte[]> map = new HashMap<BytesKey, byte[]>();
        map.put(BytesKey.createKey(key), "abc".getBytes());
        map.put(BytesKey.createKey("xxoo123"), "abc".getBytes());
        aedis.mset(listener, map);
        listener.await();
    }
}
