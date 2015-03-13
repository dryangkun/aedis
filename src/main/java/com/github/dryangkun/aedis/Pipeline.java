package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dryangkun on 15/3/11.
 */
public class Pipeline extends AedisBase {

    private final Aedis aedis;
    private final List<Command> commands;

    public Pipeline(Aedis aedis) {
        this.aedis = aedis;
        commands = new ArrayList<Command>();
    }

    public Pipeline(Aedis aedis, int size) {
        this.aedis = aedis;
        commands = new ArrayList<Command>(size);
    }

    @Override
    protected void dispatch(Command command) {
        commands.add(command);
    }

    public void syncCommands() {
        aedis.dispatch(commands);
    }
}
