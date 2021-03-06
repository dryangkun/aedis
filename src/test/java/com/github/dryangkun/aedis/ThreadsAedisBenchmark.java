package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;
import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.output.ByteArrayOutput;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by dryangkun on 15/3/11.
 */
public class ThreadsAedisBenchmark {

    public static void main(String[] args) throws Exception {
        AedisBootstrap bootstrap = new AedisBootstrap(new NioEventLoopGroup());
        bootstrap.setHostAndPort("localhost", 6379)
                .setTimeout_ms(15)
                .setCommand_queue_capacity(65535 * 4);

        final AedisGroup aedis = bootstrap.newAedisGroup(80);
        System.out.println("start...");
        final int threads = 4;
        final int count = 10000;

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicLong timeoutIncrement = new AtomicLong();
        final AtomicLong inactiveIncrement = new AtomicLong();
        final AtomicLong queuefullIncrement = new AtomicLong();
        final AtomicLong successIncrement = new AtomicLong();

        final AtomicInteger increment = new AtomicInteger(count * threads);
        final byte[] value = "1".getBytes();

        ExecutorService service = Executors.newFixedThreadPool(threads);
        long s = System.nanoTime();
        for (int i = 0; i < threads; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < count; i++) {
                        final byte[] key = i % 2 == 0 ? "xxoo1".getBytes() : "xxoo2".getBytes();

                        Pipeline pipeline = aedis.pipeline(2);
                        pipeline.get(new CommandListener<ByteArrayOutput>() {
                            @Override
                            public void operationComplete(Command<ByteArrayOutput> command) {
                                if (command.isOutputSuccess()) {
                                    if (Arrays.equals(key, command.get().getValue())) {
                                        successIncrement.incrementAndGet();
                                    }
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
                            }
                        }, key);
                        pipeline.get(new CommandListener<ByteArrayOutput>() {
                            @Override
                            public void operationComplete(Command<ByteArrayOutput> command) {
                                if (command.isOutputSuccess()) {
                                    if (Arrays.equals(key, command.get().getValue())) {
                                        successIncrement.incrementAndGet();
                                    }
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
                        }, key);
                        pipeline.syncCommands();
                    }
                }
            });
        }

        service.shutdown();
        while (!service.awaitTermination(100L, TimeUnit.MILLISECONDS));
        latch.await();
        System.out.println("stop");

        double time = (System.nanoTime() - s) / 1000.0 / 1000;
        System.out.println("total time : " + time);
        System.out.println("time : " + (time / (count * threads * 2)));
        System.out.println("per : " + (1000.0 * (count * threads * 2) / time));
        System.out.println("success : " + successIncrement.get());
        System.out.println("timeout : " + timeoutIncrement.get());
        System.out.println("inactive : " + inactiveIncrement.get());
        System.out.println("queuefull : " + queuefullIncrement.get());

        aedis.close();
        System.out.println("close");
        bootstrap.getGroup().shutdownGracefully();
        System.out.println("quit");
    }
}
