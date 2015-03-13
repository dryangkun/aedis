package com.github.dryangkun.aedis;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dryangkun on 15/3/1.
 */
public class AedisBootstrap {

    public static class Options {
        public static final int DEFAULT_TIMEOUT_MS = 1000;
        public static final int DEFAULT_CONNECT_TIMEOUT_MS = 1000;
        public static final int DEFAULT_RECONNECT_INTERVAL_MS = 1000;
        public static final int DEFAULT_COMMAND_QUEUE_SIZE = 65535;

        private String host = null;
        private int port = -1;
        private String auth = null;

        private int timeout_ms = DEFAULT_TIMEOUT_MS;
        private int connect_timeout_ms = DEFAULT_CONNECT_TIMEOUT_MS;
        private int reconnect_interval_ms = DEFAULT_RECONNECT_INTERVAL_MS;

        private int command_queue_capacity = DEFAULT_COMMAND_QUEUE_SIZE;

        public Options() {}

        public Options(Options options) {
            host = options.host;
            port = options.port;
            auth = options.auth;
            timeout_ms = options.timeout_ms;
            connect_timeout_ms = options.connect_timeout_ms;
            reconnect_interval_ms = options.reconnect_interval_ms;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getAuth() {
            return auth;
        }

        public int getTimeout_ms() {
            return timeout_ms;
        }

        public int getConnect_timeout_ms() {
            return connect_timeout_ms;
        }

        public int getReconnect_interval_ms() {
            return reconnect_interval_ms;
        }

        public int getCommand_queue_capacity() {
            return command_queue_capacity;
        }

        @Override
        @SuppressWarnings("CloneDoesntCallSuperClone")
        public Options clone() {
            return new Options(this);
        }
    }

    private final EventLoopGroup group;
    private final Options options;
    private Bootstrap bootstrap;

    public AedisBootstrap(EventLoopGroup group) {
        this.group = group;
        this.options = new Options();
    }

    public AedisBootstrap(AedisBootstrap bootstrap) {
        this.group = bootstrap.getGroup();
        this.options = bootstrap.getOptions();
        this.bootstrap = bootstrap.getBootstrap();
    }

    public EventLoopGroup getGroup() {
        return group;
    }

    public Bootstrap getBootstrap() {
        if (bootstrap == null) {
            bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_SNDBUF, 1048576)
                    .option(ChannelOption.SO_RCVBUF, 1048576)
                    .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 10 * 64 * 1024)
                    .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 10 * 64 * 1024)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_KEEPALIVE, true);
        }
        return bootstrap;
    }

    public AedisBootstrap setBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
        return this;
    }

    public AedisBootstrap setHostAndPort(String host, int port) {
        options.host = host;
        options.port = port;
        return this;
    }

    public AedisBootstrap setAuth(String auth) {
        options.auth = auth;
        return this;
    }

    public AedisBootstrap setTimeout_ms(int timeout_ms) {
        if (timeout_ms <= 0) {
            throw new IllegalArgumentException("timeout_ms <= 0:" + timeout_ms);
        }
        options.timeout_ms = timeout_ms;
        return this;
    }

    public AedisBootstrap setConnect_timeout_ms(int connect_timeout_ms) {
        options.connect_timeout_ms = connect_timeout_ms;
        return this;
    }

    public AedisBootstrap setReconnect_interval_ms(int reconnect_interval_ms) {
        options.reconnect_interval_ms = reconnect_interval_ms;
        return this;
    }

    public AedisBootstrap setCommand_queue_capacity(int command_queue_capacity) {
        options.command_queue_capacity = command_queue_capacity;
        return this;
    }

    public Options getOptions() {
        return options;
    }

    public Aedis newAedis() {
        Bootstrap bootstrap = getBootstrap().clone();
        Options options = getOptions().clone();
        final Aedis aedis = new Aedis(bootstrap.group(group), options);
        aedis.connect();
        return aedis;
    }

    public AedisGroup newAedisGroup(int size) {
        List<Aedis> aedises = new ArrayList<Aedis>(size);
        for (int i = 0; i < size; i++) {
            aedises.add(newAedis());
        }
        AedisGroup group = new AedisGroup(aedises);
        return group;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    protected AedisBootstrap clone() {
        return new AedisBootstrap(this);
    }
}
