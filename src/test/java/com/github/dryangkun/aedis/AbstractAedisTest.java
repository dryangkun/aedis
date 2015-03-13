package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;
import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.output.LongOutput;
import com.github.dryangkun.aedis.protocol.output.Output;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Created by dryangkun on 15/3/3.
 */
public abstract class AbstractAedisTest {

    public static class TestListener<A extends Output> implements CommandListener<A> {
        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void operationComplete(Command<A> command) {
            System.out.println(command);
            assertTrue(command.isOutputSuccess());
            doAssert(command.get());
            latch.countDown();
        }

        protected void doAssert(A output) {

        }

        public void await() {
            try {
                latch.await();
            } catch (InterruptedException e) {

            }
        }
    }

    protected static EventLoopGroup group;
    protected static Aedis aedis;

    protected final byte[] key = "xxoo".getBytes();
    protected TestListener listener;

    @BeforeClass
    public static void setupAedis() {
        group = new NioEventLoopGroup();
        AedisBootstrap bootstrap = new AedisBootstrap(group);
        bootstrap.setHostAndPort("localhost", 6379)
                .setAuth(null);
        aedis = bootstrap.newAedis();
    }

    @AfterClass
    public static void shutdownAedis() {
        aedis.close();
        group.shutdownGracefully();
    }

    @Before
    public void open() {
        _del(key);
    }

    protected void _del(byte[] key) {
        TestListener<LongOutput> l = new TestListener<LongOutput>();
        aedis.del(l, key);
        l.await();
    }
}
