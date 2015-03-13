package com.github.dryangkun.aedis.protocol;

import com.github.dryangkun.aedis.protocol.output.Output;

/**
 * Created by dryangkun on 15/2/28.
 */
public interface CommandListener<A extends Output> {

    public void operationComplete(Command<A> command);
}
