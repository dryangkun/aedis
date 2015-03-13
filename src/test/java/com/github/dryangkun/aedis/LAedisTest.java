package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.output.ByteArrayOutput;
import com.github.dryangkun.aedis.protocol.output.ListOutput;
import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dryangkun on 15/3/9.
 */
public class LAedisTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Override
    @Before
    public void open() {
        super.open();

        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(3), output.getValue());
            }
        };
        aedis.lpush(listener, key, "a".getBytes(), "b".getBytes(), "c".getBytes());
        listener.await();
    }

    @Test public void keys() {
        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.set(listener, key, "abc".getBytes());
        listener.await();

        listener = new TestListener<ListOutput>() {
            @Override
            protected void doAssert(ListOutput output) {
                assertNotNull(output.getList());
                List<byte[]> list = output.getList();
                boolean flag = false;
                for (byte[] bytes : list) {
                    if (Arrays.equals(bytes, key)) {
                        flag = true;
                        break;
                    }
                }
                assertTrue(flag);
            }
        };
        aedis.keys(listener, new String(key) + "*");
        listener.await();
    }

    @Test public void lastsave() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertNotNull(output.getValue());
            }
        };
        aedis.lastsave(listener);
        listener.await();
    }

    @Test public void lindex() {
        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertArrayEquals("c".getBytes(), output.getValue());
            }
        };
        aedis.lindex(listener, key, 0);
        listener.await();
    }

    @Test public void linsert() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(4), output.getValue());
            }
        };
        aedis.linsert(listener, key, true, "b".getBytes(), "d".getBytes());
        listener.await();

        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertArrayEquals("d".getBytes(), output.getValue());
            }
        };
        aedis.lindex(listener, key, 1);
        listener.await();
    }

    @Test public void llen() {
        listener = new TestListener<LongOutput>() {
            @Override
            protected void doAssert(LongOutput output) {
                assertEquals(new Long(3), output.getValue());
            }
        };
        aedis.llen(listener, key);
        listener.await();
    }

    @Test public void lpop() {
        listener = new TestListener<ByteArrayOutput>() {
            @Override
            protected void doAssert(ByteArrayOutput output) {
                assertArrayEquals("c".getBytes(), output.getValue());
            }
        };
        aedis.lpop(listener, key);
        listener.await();
    }

    @Test public void lrange() {
        listener = new TestListener<ListOutput>() {
            @Override
            protected void doAssert(ListOutput output) {
                assertNotNull(output.getList());
                List<byte[]> list = output.getList();
                assertEquals(2L, (long) list.size());
                assertArrayEquals("c".getBytes(), list.get(0));
                assertArrayEquals("b".getBytes(), list.get(1));
            }
        };
        aedis.lrange(listener, key, 0, 1);
        listener.await();
    }

    @Test public void ltrim() {
        listener = new TestListener<StatusOutput>() {
            @Override
            protected void doAssert(StatusOutput output) {
                assertTrue(output.ok());
            }
        };
        aedis.ltrim(listener, key, 0, 1);
        listener.await();
    }
}
