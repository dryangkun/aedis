package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;
import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.output.Output;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dryangkun on 15/3/11.
 */
public abstract class ListenerCombiner {

    private final Map<Integer, Command> map = new ConcurrentHashMap<Integer, Command>();
    private final AtomicInteger increment = new AtomicInteger(0);

    public <A extends Output> CommandListener<A> newListener(Class<A> clazz) {
        final int i = increment.getAndIncrement();
        return new CommandListener<A>() {
            @Override
            public void operationComplete(Command<A> command) {
                map.put(i, command);
                if (increment.decrementAndGet() == 0) {
                    ListenerCombiner.this.operationComplete(map);
                }
            }
        };
    }

    public <A extends Output> CommandListener<A> newListener() {
        final int i = increment.getAndIncrement();
        return new CommandListener<A>() {
            @Override
            public void operationComplete(Command<A> command) {
                map.put(i, command);
                if (increment.decrementAndGet() == 0) {
                    ListenerCombiner.this.operationComplete(map);
                }
            }
        };
    }

    protected abstract void operationComplete(Map<Integer, Command> map);
}
