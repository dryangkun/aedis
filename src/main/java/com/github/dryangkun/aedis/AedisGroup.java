package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dryangkun on 15/3/3.
 */
public class AedisGroup extends AedisBase implements IClosable, IPipeline {

    private final List<Aedis> aedises;
    private final AtomicInteger increment = new AtomicInteger(0);

    private volatile boolean closed = false;

    public AedisGroup(List<Aedis> aedises) {
        this.aedises = aedises;
    }

    @Override
    public void close() {
        if (!closed()) synchronized (this) {
            if (!closed()) {
                for (Aedis aedis : aedises) {
                    aedis.close();
                }
                closed = true;
            }
        }
    }

    @Override
    public boolean closed() {
        return closed;
    }

    public Aedis getAedis() {
        int i = increment.getAndIncrement();
        i = Math.abs(i % aedises.size());
        return aedises.get(i);
    }

    @Override
    public void dispatch(Command command) {
        getAedis().dispatch(command);
    }

    @Override
    public Pipeline pipeline() {
        return getAedis().pipeline();
    }

    @Override
    public Pipeline pipeline(int capacity) {
        return getAedis().pipeline(capacity);
    }
}
