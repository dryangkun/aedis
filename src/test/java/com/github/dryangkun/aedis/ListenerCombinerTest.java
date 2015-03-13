package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;
import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.output.ByteArrayOutput;
import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by dryangkun on 15/3/11.
 */
public class ListenerCombinerTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @org.junit.Test
    public void pipeline() throws Exception {
        Pipeline pipeline = aedis.pipeline();
        final CountDownLatch latch = new CountDownLatch(1);
        ListenerCombiner combiner = new ListenerCombiner() {
            @Override
            protected void operationComplete(Map<Integer, Command> map) {
                assertEquals(3L, (long) map.size());
                System.out.println(map);

                assertTrue(map.get(0).isOutputSuccess());
                assertTrue(map.get(1).isOutputSuccess());
                assertTrue(map.get(2).isOutputSuccess());

                latch.countDown();
            }
        };

        pipeline.set(combiner.newListener(StatusOutput.class), key, "123".getBytes());
        pipeline.get(combiner.newListener(ByteArrayOutput.class), key);
        pipeline.append(combiner.newListener(LongOutput.class), key, "123".getBytes());
        pipeline.syncCommands();

        latch.await();
    }
}
