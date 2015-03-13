package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;
import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.output.StatusOutput;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by dryangkun on 15/3/10.
 */
public class AedisBenchmark {

    public static void main(String[] args) throws Exception {
        AedisBootstrap bootstrap = new AedisBootstrap(new NioEventLoopGroup());
        bootstrap.setHostAndPort("localhost", 6379)
                .setTimeout_ms(10000);

        int count = 2000000;
        final AedisGroup aedis = bootstrap.newAedisGroup(80);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger increment = new AtomicInteger(count);

        final AtomicLong timeoutIncrement = new AtomicLong();
        final AtomicLong inactiveIncrement = new AtomicLong();
        final AtomicLong queuefullIncrement = new AtomicLong();
        final AtomicLong unwritableIncrement = new AtomicLong();
        final AtomicLong successIncrement = new AtomicLong();

        long s = System.nanoTime();
        for (int i = 0; i < count; i++) {
            aedis.set(new CommandListener<StatusOutput>() {
                @Override
                public void operationComplete(Command<StatusOutput> command) {
                    if (command.isOutputSuccess()) {
                        successIncrement.incrementAndGet();
                    }
                    else if (command.isTimeoutFailure()) {
                        timeoutIncrement.incrementAndGet();
                    }
                    else if (command.isInactiveFailure()) {
                        inactiveIncrement.incrementAndGet();
                    }
                    else if (command.isQueuefullFailure()) {
                        queuefullIncrement.incrementAndGet();
                    }

                    if (increment.decrementAndGet() == 0) {
                        latch.countDown();
                    }
                }
            }, "xxoo".getBytes(), "1".getBytes());
        }

        latch.await();

        double time = (System.nanoTime() - s) / 1000.0 / 1000;
        System.out.println("total time : " + time);
        System.out.println("time : " + (time / count));
        System.out.println("per : " + (1000 * count / time));
        System.out.println("success : " + successIncrement.get());
        System.out.println("timeout : " + timeoutIncrement.get());
        System.out.println("inactive : " + inactiveIncrement.get());
        System.out.println("queuefull : " + queuefullIncrement.get());
        System.out.println("unwritable : " + unwritableIncrement.get());

        aedis.close();
        bootstrap.getGroup().shutdownGracefully();
    }
}
