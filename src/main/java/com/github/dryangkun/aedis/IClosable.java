package com.github.dryangkun.aedis;

import java.io.Closeable;

/**
 * Created by dryangkun on 15/3/9.
 */
public interface IClosable extends Closeable {

    public void close();

    public boolean closed();
}
