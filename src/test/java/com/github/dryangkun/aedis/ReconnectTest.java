package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;
import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.output.ByteArrayOutput;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.CountDownLatch;

/**
 * Created by dryangkun on 15/3/13.
 */
public class ReconnectTest {

    public static void main(String[] args) throws Exception {
        AedisBootstrap bootstrap = new AedisBootstrap(new NioEventLoopGroup());
        bootstrap.setHostAndPort("localhost", 6379);

        Aedis aedis = bootstrap.newAedis();

        aedis.get(new CommandListener<ByteArrayOutput>() {
            @Override
            public void operationComplete(Command<ByteArrayOutput> command) {
                System.out.println("1 - " + command);
            }
        }, "xxoo1".getBytes());
        Thread.sleep(10000L);
        aedis.get(new CommandListener<ByteArrayOutput>() {
            @Override
            public void operationComplete(Command<ByteArrayOutput> command) {
                System.out.println("2 - " + command);
            }
        }, "xxoo1".getBytes());
        Thread.sleep(10000L);

        final CountDownLatch latch = new CountDownLatch(1);
        aedis.get(new CommandListener<ByteArrayOutput>() {
            @Override
            public void operationComplete(Command<ByteArrayOutput> command) {
                System.out.println("3 - " + command);
                latch.countDown();
            }
        }, "xxoo1".getBytes());

        latch.await();
        aedis.close();
        bootstrap.getGroup().shutdownGracefully();
    }
}
