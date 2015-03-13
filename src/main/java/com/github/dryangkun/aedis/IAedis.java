package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.output.*;

import java.util.Map;

/**
 * Created by dryangkun on 15/3/1.
 */
public interface IAedis {

    public void append(CommandListener<LongOutput> listener, byte[] key, byte[] value);

    public void bgrewriteaof(CommandListener<SingleOutput> listener);

    public void bgsave(CommandListener<SingleOutput> listener);

    public void bitcount(CommandListener<LongOutput> listener, byte[] key);

    public void bitcount(CommandListener<LongOutput> listener, byte[] key, long start, long end);

    public void bitopAnd(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys);

    public void bitopNot(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys);

    public void bitopOr(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys);

    public void bitopXor(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys);

    public void dbsize(CommandListener<LongOutput> listener);

    public void decr(CommandListener<LongOutput> listener, byte[] key);

    public void decrby(CommandListener<LongOutput> listener, byte[] key, long amount);

    public void del(CommandListener<LongOutput> listener, byte[]... keys);

    public void echo(CommandListener<ByteArrayOutput> listener, byte[] value);

    public void exists(CommandListener<BooleanOutput> listener, byte[] key);

    public void expire(CommandListener<BooleanOutput> listener, byte[] key, int seconds);

    public void expireat(CommandListener<BooleanOutput> listener, byte[] key, long timestamp);

    public void get(CommandListener<ByteArrayOutput> listener, byte[] key);

    public void getbit(CommandListener<LongOutput> listener, byte[] key, long offset);

    public void getrange(CommandListener<ByteArrayOutput> listener, byte[] key, long start, long end);

    public void getset(CommandListener<ByteArrayOutput> listener, byte[] key, byte[] value);

    public void hdel(CommandListener<LongOutput> listener, byte[] key, byte[]... fields);

    public void hexists(CommandListener<BooleanOutput> listener, byte[] key, byte[] field);

    public void hget(CommandListener<ByteArrayOutput> listener, byte[] key, byte[] field);

    public void hincrby(CommandListener<LongOutput> listener, byte[] key, byte[] field, long amount);

    public void hincrbyfloat(CommandListener<DoubleOutput> listener, byte[] key, byte[] field, double amount);

    public void hgetAll(CommandListener<MapOutput> listener, byte[] key);

    public void hkeys(CommandListener<ListOutput> listener, byte[] key);

    public void hlen(CommandListener<LongOutput> listener, byte[] key);

    public void hmget(CommandListener<ListOutput> listener, byte[] key, byte[]... fields);

    public void hmset(CommandListener<StatusOutput> listener, byte[] key, Map<BytesKey, byte[]> map);

    public void hset(CommandListener<BooleanOutput> listener, byte[] key, byte[] field, byte[] value);

    public void hsetnx(CommandListener<BooleanOutput> listener, byte[] key, byte[] field, byte[] value);

    public void hvals(CommandListener<ListOutput> listener, byte[] key);

    public void incr(CommandListener<LongOutput> listener, byte[] key);

    public void incrby(CommandListener<LongOutput> listener, byte[] key, long amount);

    public void incrbyfloat(CommandListener<DoubleOutput> listener, byte[] key, double amount);

    public void keys(CommandListener<ListOutput> listener, String pattern);

    public void lastsave(CommandListener<LongOutput> listener);

    public void lindex(CommandListener<ByteArrayOutput> listener, byte[] key, long index);

    public void linsert(CommandListener<LongOutput> listener, byte[] key, boolean before, byte[] pivot, byte[] value);

    public void llen(CommandListener<LongOutput> listener, byte[] key);

    public void lpop(CommandListener<ByteArrayOutput> listener, byte[] key);

    public void lpush(CommandListener<LongOutput> listener, byte[] key, byte[]... values);

    public void lpushx(CommandListener<LongOutput> listener, byte[] key, byte[]... values);

    public void lrange(CommandListener<ListOutput> listener, byte[] key, long start, long stop);

    public void lrem(CommandListener<LongOutput> listener, byte[] key, long count, byte[] value);

    public void lset(CommandListener<StatusOutput> listener, byte[] key, long index, byte[] value);

    public void ltrim(CommandListener<StatusOutput> listener, byte[] key, long start, long stop);

    public void migrate(CommandListener<StatusOutput> listener, String host, int port, byte[] key, int db, long timeout);

    public void mget(CommandListener<ListOutput> listener, byte[]... keys);

    public void move(CommandListener<BooleanOutput> listener, byte[] key, int db);

    public void mset(CommandListener<StatusOutput> listener, Map<BytesKey, byte[]> map);

    public void msetnx(CommandListener<StatusOutput> listener, Map<BytesKey, byte[]> map);

    public void objectEncoding(CommandListener<StatusOutput> listener, byte[] key);

    public void objectIdletime(CommandListener<LongOutput> listener, byte[] key);

    public void objectRefcount(CommandListener<LongOutput> listener, byte[] key);

    public void persist(CommandListener<BooleanOutput> listener, byte[] key);

    public void pexpire(CommandListener<BooleanOutput> listener, byte[] key, long milliseconds);

    public void pexpireat(CommandListener<BooleanOutput> listener, byte[] key, long millitimestamp);

    public void ping(CommandListener<SingleOutput> listener);

    public void pttl(CommandListener<LongOutput> listener, byte[] key);

    public void quit(CommandListener<StatusOutput> listener);

    public void randomkey(CommandListener<ByteArrayOutput> listener);

    public void rename(CommandListener<StatusOutput> listener, byte[] key1, byte[] key2);

    public void renamenx(CommandListener<StatusOutput> listener, byte[] key1, byte[] key2);

    public void restore(CommandListener<StatusOutput> listener, byte[] key, long ttl, byte[] value);

    public void rpop(CommandListener<ByteArrayOutput> listener, byte[] key);

    public void rpoplpush(CommandListener<ByteArrayOutput> listener, byte[] src, byte[] dest);

    public void rpush(CommandListener<LongOutput> listener, byte[] key, byte[]... values);

    public void rpushx(CommandListener<LongOutput> listener, byte[] key, byte[]... values);

    public void sadd(CommandListener<LongOutput> listener, byte[] key, byte[]... members);

    public void save(CommandListener<StatusOutput> listener);

    public void scard(CommandListener<LongOutput> listener, byte[] key);

    public void sdiff(CommandListener<SetOutput> listener, byte[]... keys);

    public void sdiffstore(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys);

    public void select(CommandListener<StatusOutput> listener, int db);

    public void set(CommandListener<StatusOutput> listener, byte[] key, byte[] value);

    public void setbit(CommandListener<LongOutput> listener, byte[] key, long offset, int value);

    public void setex(CommandListener<StatusOutput> listener, byte[] key, int seconds, byte[] value);

    public void setnx(CommandListener<BooleanOutput> listener, byte[] key, byte[] value);

    public void setrange(CommandListener<LongOutput> listener, byte[] key, long offset, byte[] value);

    public void sinter(CommandListener<SetOutput> listener, byte[]... keys);

    public void sinterstore(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys);

    public void sismember(CommandListener<BooleanOutput> listener, byte[] key, byte[] member);

    public void smove(CommandListener<BooleanOutput> listener, byte[] src, byte[] dest, byte[] member);

    public void slaveof(CommandListener<StatusOutput> listener, String host, int port);

    public void smembers(CommandListener<SetOutput> listener, byte[] key);

    public void spop(CommandListener<ByteArrayOutput> listener, byte[] key);

    public void srandmember(CommandListener<ByteArrayOutput> listener, byte[] key);

    public void srandmember(CommandListener<SetOutput> listener, byte[] key, long count);

    public void srem(CommandListener<LongOutput> listener, byte[] key, byte[]... members);

    public void sunion(CommandListener<SetOutput> listener, byte[]... keys);

    public void sunionstore(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys);

    public void sync(CommandListener<StatusOutput> listener);

    public void strlen(CommandListener<LongOutput> listener, byte[] key);

    public void ttl(CommandListener<LongOutput> listener, byte[] key);

    public void type(CommandListener<SingleOutput> listener, byte[] key);

    public void zadd(CommandListener<LongOutput> listener, byte[] key, double score, byte[] member);

    public void zadd(CommandListener<LongOutput> listener, byte[] key, ZMember... members);

    public void zcard(CommandListener<LongOutput> listener, byte[] key);

    public void zcount(CommandListener<LongOutput> listener, byte[] key, double min, double max);

    public void zincrby(CommandListener<DoubleOutput> listener, byte[] key, double amount, byte[] member);

    public void zrange(CommandListener<ListOutput> listener, byte[] key, long start, long stop);

    public void zrangeWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, long start, long stop);

    public void zrangebyscore(CommandListener<ListOutput> listener, byte[] key, double min, double max);

    public void zrangebyscore(CommandListener<ListOutput> listener, byte[] key, double min, double max, long offset, long count);

    public void zrangebyscoreWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, double min, double max);

    public void zrangebyscoreWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, double min, double max, long offset, long count);

    public void zrank(CommandListener<LongOutput> listener, byte[] key, byte[] member);

    public void zrem(CommandListener<LongOutput> listener, byte[] key, byte[]... members);

    public void zremrangebyrank(CommandListener<LongOutput> listener, byte[] key, long start, long stop);

    public void zremrangebyscore(CommandListener<LongOutput> listener, byte[] key, double min, double max);

    public void zrevrange(CommandListener<ListOutput> listener, byte[] key, long start, long stop);

    public void zrevrangeWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, long start, long stop);

    public void zrevrangebyscore(CommandListener<ListOutput> listener, byte[] key, double min, double max);

    public void zrevrangebyscore(CommandListener<ListOutput> listener, byte[] key, double min, double max, long offset, long count);

    public void zrevrangebyscoreWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, double min, double max);

    public void zrevrangebyscoreWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, double min, double max, long offset, long count);

    public void zrevrank(CommandListener<LongOutput> listener, byte[] key, byte[] member);

    public void zscore(CommandListener<DoubleOutput> listener, byte[] key, byte[] member);
}
