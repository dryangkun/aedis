// Copyright (C) 2011 - Will Glozer.  All rights reserved.

package com.github.dryangkun.aedis.protocol;

import io.netty.util.CharsetUtil;

/**
 * Keyword modifiers for redis commands.
 *
 * @author Will Glozer
 */
public enum CommandKeyword {
    AFTER, AGGREGATE, ALPHA, AND, ASC, BEFORE, BY, COUNT, DESC, ENCODING, FLUSH,
    GETNAME, IDLETIME, KILL, LEN, LIMIT, LIST, LOAD, MAX, MIN, NO, NOSAVE, NOT,
    ONE, OR, REFCOUNT, RESET, RESETSTAT, SETNAME, STORE, SUM, WEIGHTS,
    WITHSCORES, XOR;

    public byte[] bytes;

    private CommandKeyword() {
        bytes = name().getBytes(CharsetUtil.US_ASCII);
    }
}
