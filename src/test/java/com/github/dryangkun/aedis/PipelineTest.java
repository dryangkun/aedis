package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;
import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.output.ByteArrayOutput;
import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by dryangkun on 15/3/9.
 */
public class PipelineTest extends AbstractAedisTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void pipeline() throws Exception {
        Pipeline pipeline = aedis.pipeline();
        final CountDownLatch latch = new CountDownLatch(3);

        pipeline.set(new CommandListener<StatusOutput>() {
            @Override
            public void operationComplete(Command<StatusOutput> command) {
                System.out.println(command);
                assertTrue(command.isOutputSuccess());
                assertTrue(command.get().ok());
                latch.countDown();
            }
        }, key, "123".getBytes());
        pipeline.get(new CommandListener<ByteArrayOutput>() {
            @Override
            public void operationComplete(Command<ByteArrayOutput> command) {
                System.out.println(command);
                assertTrue(command.isOutputSuccess());
                latch.countDown();
            }
        }, key);
        pipeline.append(new CommandListener<LongOutput>() {
            @Override
            public void operationComplete(Command<LongOutput> command) {
                System.out.println(command);
                assertTrue(command.isOutputSuccess());
                assertNotNull(command.get().getValue());
                latch.countDown();
            }
        }, key, "123".getBytes());
        pipeline.syncCommands();

        latch.await();
    }
}
