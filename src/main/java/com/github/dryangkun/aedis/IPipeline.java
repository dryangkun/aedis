package com.github.dryangkun.aedis;

/**
 * Created by dryangkun on 15/3/11.
 */
public interface IPipeline {

    public Pipeline pipeline();

    public Pipeline pipeline(int capacity);
}
