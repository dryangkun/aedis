package com.github.dryangkun.aedis;

import com.github.dryangkun.aedis.protocol.Command;
import com.github.dryangkun.aedis.protocol.CommandListener;
import com.github.dryangkun.aedis.protocol.output.*;

import java.util.Map;

import static com.github.dryangkun.aedis.protocol.CommandKeyword.*;
import static com.github.dryangkun.aedis.protocol.CommandType.*;

/**
 * Created by dryangkun on 15/3/9.
 */
public abstract class AedisBase implements IAedis {

    protected abstract void dispatch(Command command);

    public void append(CommandListener<LongOutput> listener, byte[] key, byte[] value) {
        Command<LongOutput> command = new Command<LongOutput>(
                APPEND, listener, new LongOutput(),
                key, value);
        dispatch(command);
    }

    @Override
    public void bgrewriteaof(CommandListener<SingleOutput> listener) {
        Command<SingleOutput> command = new Command<SingleOutput>(
                BGREWRITEAOF, listener, new SingleOutput());
        dispatch(command);
    }

    @Override
    public void bgsave(CommandListener<SingleOutput> listener) {
        Command<SingleOutput> command = new Command<SingleOutput>(
                BGSAVE, listener, new SingleOutput());
        dispatch(command);
    }

    @Override
    public void bitcount(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(
                BITCOUNT, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void bitcount(CommandListener<LongOutput> listener, byte[] key, long start, long end) {
        Command<LongOutput> command = new Command<LongOutput>(
                BITCOUNT, listener, new LongOutput(), 3);
        command.addArg(key).addArg(start).addArg(end);
        dispatch(command);
    }

    @Override
    public void bitopAnd(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys) {
        Command<LongOutput> command = new Command<LongOutput>(
                BITOP, listener, new LongOutput(), 2 + keys.length);
        command.addArg(AND).addArg(dest).addArgs(keys);
        dispatch(command);
    }

    @Override
    public void bitopNot(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys) {
        Command<LongOutput> command = new Command<LongOutput>(
                BITOP, listener, new LongOutput(), 2 + keys.length);
        command.addArg(NOT).addArg(dest).addArgs(keys);
        dispatch(command);
    }

    @Override
    public void bitopOr(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys) {
        Command<LongOutput> command = new Command<LongOutput>(
                BITOP, listener, new LongOutput(), 2 + keys.length);
        command.addArg(OR).addArg(dest).addArgs(keys);
        dispatch(command);
    }

    @Override
    public void bitopXor(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys) {
        Command<LongOutput> command = new Command<LongOutput>(
                BITOP, listener, new LongOutput(), 2 + keys.length);
        command.addArg(XOR).addArg(dest).addArgs(keys);
        dispatch(command);
    }

    @Override
    public void dbsize(CommandListener<LongOutput> listener) {
        Command<LongOutput> command = new Command<LongOutput>(
                DBSIZE, listener, new LongOutput());
        dispatch(command);
    }

    @Override
    public void decr(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(
                DECR, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void decrby(CommandListener<LongOutput> listener, byte[] key, long amount) {
        Command<LongOutput> command = new Command<LongOutput>(
                DECRBY, listener, new LongOutput(), 2);
        command.addArg(key).addArg(amount);
        dispatch(command);
    }

    @Override
    public void del(CommandListener<LongOutput> listener, byte[]... keys) {
        Command<LongOutput> command = new Command<LongOutput>(
                DEL, listener, new LongOutput(), keys);
        dispatch(command);
    }

    @Override
    public void echo(CommandListener<ByteArrayOutput> listener, byte[] value) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(
                ECHO, listener, new ByteArrayOutput(), value);
        dispatch(command);
    }

    @Override
    public void exists(CommandListener<BooleanOutput> listener, byte[] key) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(
                EXISTS, listener, new BooleanOutput(), key);
        dispatch(command);
    }

    @Override
    public void expire(CommandListener<BooleanOutput> listener, byte[] key, int seconds) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(
                EXPIRE, listener, new BooleanOutput(), 2);
        command.addArg(key).addArg(seconds);
        dispatch(command);
    }

    @Override
    public void expireat(CommandListener<BooleanOutput> listener, byte[] key, long timestamp) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(
                EXISTS, listener, new BooleanOutput(), 2);
        command.addArg(key).addArg(timestamp);
        dispatch(command);
    }

    @Override
    public void get(CommandListener<ByteArrayOutput> listener, byte[] key) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(
                GET, listener, new ByteArrayOutput(), key);
        dispatch(command);
    }

    @Override
    public void getbit(CommandListener<LongOutput> listener, byte[] key, long offset) {
        Command<LongOutput> command = new Command<LongOutput>(
                GETBIT, listener, new LongOutput(), 2);
        command.addArg(key).addArg(offset);
        dispatch(command);
    }

    @Override
    public void getrange(CommandListener<ByteArrayOutput> listener, byte[] key, long start, long end) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(
                GET, listener, new ByteArrayOutput(), 3);
        command.addArg(key).addArg(start).addArg(end);
        dispatch(command);
    }

    @Override
    public void getset(CommandListener<ByteArrayOutput> listener, byte[] key, byte[] value) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(
                GETSET, listener, new ByteArrayOutput(), key, value);
        dispatch(command);
    }

    @Override
    public void hdel(CommandListener<LongOutput> listener, byte[] key, byte[]... fields) {
        Command<LongOutput> command = new Command<LongOutput>(
                HDEL, listener, new LongOutput(), 1 + fields.length);
        command.addArg(key).addArgs(fields);
        dispatch(command);
    }

    @Override
    public void hexists(CommandListener<BooleanOutput> listener, byte[] key, byte[] field) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(HEXISTS, listener, new BooleanOutput(), key, field);
        dispatch(command);
    }

    @Override
    public void hget(CommandListener<ByteArrayOutput> listener, byte[] key, byte[] field) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(HGET, listener, new ByteArrayOutput(), key, field);
        dispatch(command);
    }

    @Override
    public void hincrby(CommandListener<LongOutput> listener, byte[] key, byte[] field, long amount) {
        Command<LongOutput> command = new Command<LongOutput>(
                HINCRBY, listener, new LongOutput(), 3);
        command.addArg(key).addArg(field).addArg(amount);
        dispatch(command);
    }

    @Override
    public void hincrbyfloat(CommandListener<DoubleOutput> listener, byte[] key, byte[] field, double amount) {
        Command<DoubleOutput> command = new Command<DoubleOutput>(
                HINCRBYFLOAT, listener, new DoubleOutput(), 3);
        command.addArg(key).addArg(field).addArg(amount);
        dispatch(command);
    }

    @Override
    public void hgetAll(CommandListener<MapOutput> listener, byte[] key) {
        Command<MapOutput> command = new Command<MapOutput>(HGETALL, listener, new MapOutput(), key);
        dispatch(command);
    }

    @Override
    public void hkeys(CommandListener<ListOutput> listener, byte[] key) {
        Command<ListOutput> command = new Command<ListOutput>(HKEYS, listener, new ListOutput(), key);
        dispatch(command);
    }

    @Override
    public void hlen(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(HLEN, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void hmget(CommandListener<ListOutput> listener, byte[] key, byte[]... fields) {
        Command<ListOutput> command = new Command<ListOutput>(HMGET, listener, new ListOutput(), 1 + fields.length);
        command.addArg(key).addArgs(fields);
        dispatch(command);
    }

    @Override
    public void hmset(CommandListener<StatusOutput> listener, byte[] key, Map<BytesKey, byte[]> map) {
        Command<StatusOutput> command = new Command<StatusOutput>(HMSET, listener, new StatusOutput(), map.size() * 2 + 1);
        command.addArg(key);
        for (BytesKey k : map.keySet()) {
            command.addArg(k.value).addArg(map.get(k));
        }
        dispatch(command);
    }

    @Override
    public void hset(CommandListener<BooleanOutput> listener, byte[] key, byte[] field, byte[] value) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(HSET, listener, new BooleanOutput(), key, field, value);
        dispatch(command);
    }

    @Override
    public void hsetnx(CommandListener<BooleanOutput> listener, byte[] key, byte[] field, byte[] value) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(HSETNX, listener, new BooleanOutput(), key, field, value);
        dispatch(command);
    }

    @Override
    public void hvals(CommandListener<ListOutput> listener, byte[] key) {
        Command<ListOutput> command = new Command<ListOutput>(HVALS, listener, new ListOutput(), key);
        dispatch(command);
    }

    @Override
    public void incr(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(INCR, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void incrby(CommandListener<LongOutput> listener, byte[] key, long amount) {
        Command<LongOutput> command = new Command<LongOutput>(INCRBY, listener, new LongOutput(), 2);
        command.addArg(key).addArg(amount);
        dispatch(command);
    }

    @Override
    public void incrbyfloat(CommandListener<DoubleOutput> listener, byte[] key, double amount) {
        Command<DoubleOutput> command = new Command<DoubleOutput>(INCRBYFLOAT, listener, new DoubleOutput(), 2);
        command.addArg(key).addArg(amount);
        dispatch(command);
    }

    @Override
    public void keys(CommandListener<ListOutput> listener, String pattern) {
        Command<ListOutput> command  = new Command<ListOutput>(KEYS, listener, new ListOutput(), pattern.getBytes(Charsets.ASCII));
        dispatch(command);
    }

    @Override
    public void lastsave(CommandListener<LongOutput> listener) {
        Command<LongOutput> command = new Command<LongOutput>(LASTSAVE, listener, new LongOutput());
        dispatch(command);
    }

    @Override
    public void lindex(CommandListener<ByteArrayOutput> listener, byte[] key, long index) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(LINDEX, listener, new ByteArrayOutput(), 2);
        command.addArg(key).addArg(index);
        dispatch(command);
    }

    @Override
    public void linsert(CommandListener<LongOutput> listener, byte[] key, boolean before, byte[] pivot, byte[] value) {
        Command<LongOutput> command = new Command<LongOutput>(LINSERT, listener, new LongOutput(), 4);
        command.addArg(key).addArg(before ? BEFORE : AFTER).addArg(pivot).addArg(value);
        dispatch(command);
    }

    @Override
    public void llen(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(LLEN, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void lpop(CommandListener<ByteArrayOutput> listener, byte[] key) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(LPOP, listener, new ByteArrayOutput(), key);
        dispatch(command);
    }

    @Override
    public void lpush(CommandListener<LongOutput> listener, byte[] key, byte[]... values) {
        Command<LongOutput> command = new Command<LongOutput>(LPUSH, listener, new LongOutput(), 1 + values.length);
        command.addArg(key).addArgs(values);
        dispatch(command);
    }

    @Override
    public void lpushx(CommandListener<LongOutput> listener, byte[] key, byte[]... values) {
        Command<LongOutput> command = new Command<LongOutput>(LPUSHX, listener, new LongOutput(), 1 + values.length);
        command.addArg(key).addArgs(values);
        dispatch(command);
    }

    @Override
    public void lrange(CommandListener<ListOutput> listener, byte[] key, long start, long stop) {
        Command<ListOutput> command = new Command<ListOutput>(LRANGE, listener, new ListOutput(), 3);
        command.addArg(key).addArg(start).addArg(stop);
        dispatch(command);
    }

    @Override
    public void lrem(CommandListener<LongOutput> listener, byte[] key, long count, byte[] value) {
        Command<LongOutput> command = new Command<LongOutput>(LREM, listener, new LongOutput(), 3);
        command.addArg(key).addArg(count).addArg(value);
        dispatch(command);
    }

    @Override
    public void lset(CommandListener<StatusOutput> listener, byte[] key, long index, byte[] value) {
        Command<StatusOutput> command = new Command<StatusOutput>(LSET, listener, new StatusOutput(), 3);
        command.addArg(key).addArg(index).addArg(value);
        dispatch(command);
    }

    @Override
    public void ltrim(CommandListener<StatusOutput> listener, byte[] key, long start, long stop) {
        Command<StatusOutput> command = new Command<StatusOutput>(LTRIM, listener, new StatusOutput(), 3);
        command.addArg(key).addArg(start).addArg(stop);
        dispatch(command);
    }

    @Override
    public void migrate(CommandListener<StatusOutput> listener, String host, int port, byte[] key, int db, long timeout) {
        Command<StatusOutput> command = new Command<StatusOutput>(MIGRATE, listener, new StatusOutput(), 5);
        command.addArg(host).addArg(port).addArg(key).addArg(db).addArg(timeout);
        dispatch(command);
    }

    @Override
    public void mget(CommandListener<ListOutput> listener, byte[]... keys) {
        Command<ListOutput> command = new Command<ListOutput>(MGET, listener, new ListOutput(), keys);
        dispatch(command);
    }

    @Override
    public void move(CommandListener<BooleanOutput> listener, byte[] key, int db) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(MOVE, listener, new BooleanOutput(), 2);
        command.addArg(key).addArg(db);
        dispatch(command);
    }

    @Override
    public void mset(CommandListener<StatusOutput> listener, Map<BytesKey, byte[]> map) {
        Command<StatusOutput> command = new Command<StatusOutput>(MSET, listener, new StatusOutput(), map.size());
        for (BytesKey k : map.keySet()) {
            command.addArg(k.value).addArg(map.get(k));
        }
        dispatch(command);
    }

    @Override
    public void msetnx(CommandListener<StatusOutput> listener, Map<BytesKey, byte[]> map) {
        Command<StatusOutput> command = new Command<StatusOutput>(MSETNX, listener, new StatusOutput(), map.size());
        for (BytesKey k : map.keySet()) {
            command.addArg(k.value).addArg(map.get(k));
        }
        dispatch(command);
    }

    @Override
    public void objectEncoding(CommandListener<StatusOutput> listener, byte[] key) {
        Command<StatusOutput> command = new Command<StatusOutput>(OBJECT, listener, new StatusOutput(), ENCODING.bytes, key);
        dispatch(command);
    }

    @Override
    public void objectIdletime(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(OBJECT, listener, new LongOutput(), IDLETIME.bytes, key);
        dispatch(command);
    }

    @Override
    public void objectRefcount(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(OBJECT, listener, new LongOutput(), REFCOUNT.bytes, key);
        dispatch(command);
    }

    @Override
    public void persist(CommandListener<BooleanOutput> listener, byte[] key) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(PERSIST, listener, new BooleanOutput(), key);
        dispatch(command);
    }

    @Override
    public void pexpire(CommandListener<BooleanOutput> listener, byte[] key, long milliseconds) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(PEXPIRE, listener, new BooleanOutput(), 2);
        command.addArg(key).addArg(milliseconds);
        dispatch(command);
    }

    @Override
    public void pexpireat(CommandListener<BooleanOutput> listener, byte[] key, long millitimestamp) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(PEXPIREAT, listener, new BooleanOutput(), 2);
        command.addArg(key).addArg(millitimestamp);
        dispatch(command);
    }

    @Override
    public void ping(CommandListener<SingleOutput> listener) {
        Command<SingleOutput> command = new Command<SingleOutput>(PING, listener, new SingleOutput());
        dispatch(command);
    }

    @Override
    public void pttl(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(PTTL, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void quit(CommandListener<StatusOutput> listener) {
        Command<StatusOutput> command = new Command<StatusOutput>(QUIT, listener, new StatusOutput());
        dispatch(command);
    }

    @Override
    public void randomkey(CommandListener<ByteArrayOutput> listener) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(RANDOMKEY, listener, new ByteArrayOutput());
        dispatch(command);
    }

    @Override
    public void rename(CommandListener<StatusOutput> listener, byte[] key1, byte[] key2) {
        Command<StatusOutput> command = new Command<StatusOutput>(RENAME, listener, new StatusOutput(), key1, key2);
        dispatch(command);
    }

    @Override
    public void renamenx(CommandListener<StatusOutput> listener, byte[] key1, byte[] key2) {
        Command<StatusOutput> command = new Command<StatusOutput>(RENAMENX, listener, new StatusOutput(), key1, key2);
        dispatch(command);
    }

    @Override
    public void restore(CommandListener<StatusOutput> listener, byte[] key, long ttl, byte[] value) {
        Command<StatusOutput> command = new Command<StatusOutput>(RESTORE, listener, new StatusOutput(), 3);
        command.addArg(key).addArg(ttl).addArg(value);
        dispatch(command);
    }

    @Override
    public void rpop(CommandListener<ByteArrayOutput> listener, byte[] key) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(RPOP, listener, new ByteArrayOutput(), key);
        dispatch(command);
    }

    @Override
    public void rpoplpush(CommandListener<ByteArrayOutput> listener, byte[] src, byte[] dest) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(RPOPLPUSH, listener, new ByteArrayOutput(), src, dest);
        dispatch(command);
    }

    @Override
    public void rpush(CommandListener<LongOutput> listener, byte[] key, byte[]... values) {
        Command<LongOutput> command = new Command<LongOutput>(RPUSH, listener, new LongOutput(), 1 + values.length);
        command.addArg(key).addArgs(values);
        dispatch(command);
    }

    @Override
    public void rpushx(CommandListener<LongOutput> listener, byte[] key, byte[]... values) {
        Command<LongOutput> command = new Command<LongOutput>(RPUSHX, listener, new LongOutput(), 1 + values.length);
        command.addArg(key).addArgs(values);
        dispatch(command);
    }

    @Override
    public void sadd(CommandListener<LongOutput> listener, byte[] key, byte[]... members) {
        Command<LongOutput> command = new Command<LongOutput>(SADD, listener, new LongOutput(), 1 + members.length);
        command.addArg(key).addArgs(members);
        dispatch(command);
    }

    @Override
    public void save(CommandListener<StatusOutput> listener) {
        Command<StatusOutput> command = new Command<StatusOutput>(SAVE, listener, new StatusOutput());
        dispatch(command);
    }

    @Override
    public void scard(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(SCARD, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void sdiff(CommandListener<SetOutput> listener, byte[]... keys) {
        Command<SetOutput> command = new Command<SetOutput>(SDIFF, listener, new SetOutput(), keys);
        dispatch(command);
    }

    @Override
    public void sdiffstore(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys) {
        Command<LongOutput> command = new Command<LongOutput>(SDIFFSTORE, listener, new LongOutput(), 1 + keys.length);
        command.addArg(dest).addArgs(keys);
        dispatch(command);
    }

    @Override
    public void select(CommandListener<StatusOutput> listener, int db) {
        Command<StatusOutput> command = new Command<StatusOutput>(SELECT, listener, new StatusOutput(), 1);
        command.addArg(db);
        dispatch(command);
    }

    @Override
    public void set(CommandListener<StatusOutput> listener, byte[] key, byte[] value) {
        Command<StatusOutput> command = new Command<StatusOutput>(SET, listener, new StatusOutput(), key, value);
        dispatch(command);
    }

    @Override
    public void setbit(CommandListener<LongOutput> listener, byte[] key, long offset, int value) {
        Command<LongOutput> command = new Command<LongOutput>(SETBIT, listener, new LongOutput(), 3);
        command.addArg(key).addArg(offset).addArg(value);
        dispatch(command);
    }

    @Override
    public void setex(CommandListener<StatusOutput> listener, byte[] key, int seconds, byte[] value) {
        Command<StatusOutput> command = new Command<StatusOutput>(SETEX, listener, new StatusOutput(), 3);
        command.addArg(key).addArg(seconds).addArg(value);
        dispatch(command);
    }

    @Override
    public void setnx(CommandListener<BooleanOutput> listener, byte[] key, byte[] value) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(SETNX, listener, new BooleanOutput(), key, value);
        dispatch(command);
    }

    @Override
    public void setrange(CommandListener<LongOutput> listener, byte[] key, long offset, byte[] value) {
        Command<LongOutput> command = new Command<LongOutput>(SETRANGE, listener, new LongOutput(), 3);
        command.addArg(key).addArg(offset).addArg(value);
        dispatch(command);
    }

    @Override
    public void sinter(CommandListener<SetOutput> listener, byte[]... keys) {
        Command<SetOutput> command = new Command<SetOutput>(SINTER, listener, new SetOutput(), keys);
        dispatch(command);
    }

    @Override
    public void sinterstore(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys) {
        Command<LongOutput> command = new Command<LongOutput>(SINTERSTORE, listener, new LongOutput(), 1 + keys.length);
        command.addArg(dest).addArgs(keys);
        dispatch(command);
    }

    @Override
    public void sismember(CommandListener<BooleanOutput> listener, byte[] key, byte[] member) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(SISMEMBER, listener, new BooleanOutput(), key, member);
        dispatch(command);
    }

    @Override
    public void smove(CommandListener<BooleanOutput> listener, byte[] src, byte[] dest, byte[] member) {
        Command<BooleanOutput> command = new Command<BooleanOutput>(SMOVE, listener, new BooleanOutput(), src, dest, member);
        dispatch(command);
    }

    @Override
    public void slaveof(CommandListener<StatusOutput> listener, String host, int port) {
        Command<StatusOutput> command = new Command<StatusOutput>(SLAVEOF, listener, new StatusOutput(), 2);
        command.addArg(host).addArg(port);
        dispatch(command);
    }

    @Override
    public void smembers(CommandListener<SetOutput> listener, byte[] key) {
        Command<SetOutput> command = new Command<SetOutput>(SMEMBERS, listener, new SetOutput(), key);
        dispatch(command);
    }

    @Override
    public void spop(CommandListener<ByteArrayOutput> listener, byte[] key) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(SPOP, listener, new ByteArrayOutput(), key);
        dispatch(command);
    }

    @Override
    public void srandmember(CommandListener<ByteArrayOutput> listener, byte[] key) {
        Command<ByteArrayOutput> command = new Command<ByteArrayOutput>(SRANDMEMBER, listener, new ByteArrayOutput(), key);
        dispatch(command);
    }

    @Override
    public void srandmember(CommandListener<SetOutput> listener, byte[] key, long count) {
        Command<SetOutput> command = new Command<SetOutput>(SRANDMEMBER, listener, new SetOutput(), 2);
        command.addArg(key).addArg(count);
        dispatch(command);
    }

    @Override
    public void srem(CommandListener<LongOutput> listener, byte[] key, byte[]... members) {
        Command<LongOutput> command = new Command<LongOutput>(SREM, listener, new LongOutput(), 1 + members.length);
        command.addArg(key).addArgs(members);
        dispatch(command);
    }

    @Override
    public void sunion(CommandListener<SetOutput> listener, byte[]... keys) {
        Command<SetOutput> command = new Command<SetOutput>(SUNION, listener, new SetOutput(), keys);
        dispatch(command);
    }

    @Override
    public void sunionstore(CommandListener<LongOutput> listener, byte[] dest, byte[]... keys) {
        Command<LongOutput> command = new Command<LongOutput>(SUNIONSTORE, listener, new LongOutput(), 1 + keys.length);
        command.addArg(dest).addArgs(keys);
        dispatch(command);
    }

    @Override
    public void sync(CommandListener<StatusOutput> listener) {
        Command<StatusOutput> command = new Command<StatusOutput>(SYNC, listener, new StatusOutput());
        dispatch(command);
    }

    @Override
    public void strlen(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(STRLEN, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void ttl(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(TTL, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void type(CommandListener<SingleOutput> listener, byte[] key) {
        Command<SingleOutput> command = new Command<SingleOutput>(TYPE, listener, new SingleOutput(), key);
        dispatch(command);
    }

    @Override
    public void zadd(CommandListener<LongOutput> listener, byte[] key, double score, byte[] member) {
        Command<LongOutput> command = new Command<LongOutput>(ZADD, listener, new LongOutput(), 3);
        command.addArg(key).addArg(score).addArg(member);
        dispatch(command);
    }

    @Override
    public void zadd(CommandListener<LongOutput> listener, byte[] key, ZMember... members) {
        Command<LongOutput> command = new Command<LongOutput>(ZADD, listener, new LongOutput(), 1 + members.length * 2);
        command.addArg(key);
        for (ZMember member : members) {
            command.addArg(member.score).addArg(member.member);
        }
        dispatch(command);
    }

    @Override
    public void zcard(CommandListener<LongOutput> listener, byte[] key) {
        Command<LongOutput> command = new Command<LongOutput>(ZCARD, listener, new LongOutput(), key);
        dispatch(command);
    }

    @Override
    public void zcount(CommandListener<LongOutput> listener, byte[] key, double min, double max) {
        Command<LongOutput> command = new Command<LongOutput>(ZCOUNT, listener, new LongOutput(), 3);
        command.addArg(key).addArg(min).addArg(max);
        dispatch(command);
    }

    @Override
    public void zincrby(CommandListener<DoubleOutput> listener, byte[] key, double amount, byte[] member) {
        Command<DoubleOutput> command = new Command<DoubleOutput>(ZINCRBY, listener, new DoubleOutput(), 3);
        command.addArg(key).addArg(amount).addArg(member);
        dispatch(command);
    }

    @Override
    public void zrange(CommandListener<ListOutput> listener, byte[] key, long start, long stop) {
        Command<ListOutput> command = new Command<ListOutput>(ZRANGE, listener, new ListOutput(), 3);
        command.addArg(key).addArg(start).addArg(stop);
        dispatch(command);
    }

    @Override
    public void zrangeWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, long start, long stop) {
        Command<ZMemberListOutput> command = new Command<ZMemberListOutput>(ZRANGE, listener, new ZMemberListOutput(), 4);
        command.addArg(key).addArg(start).addArg(stop).addArg(WITHSCORES);
        dispatch(command);
    }

    @Override
    public void zrangebyscore(CommandListener<ListOutput> listener, byte[] key, double min, double max) {
        Command<ListOutput> command = new Command<ListOutput>(ZRANGEBYSCORE, listener, new ListOutput(), 3);
        command.addArg(key).addArg(min).addArg(max);
        dispatch(command);
    }

    @Override
    public void zrangebyscore(CommandListener<ListOutput> listener, byte[] key, double min, double max, long offset, long count) {
        Command<ListOutput> command = new Command<ListOutput>(ZRANGEBYSCORE, listener, new ListOutput(), 6);
        command.addArg(key).addArg(min).addArg(max);
        command.addArg(LIMIT).addArg(offset).addArg(count);
        dispatch(command);
    }

    @Override
    public void zrangebyscoreWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, double min, double max) {
        Command<ZMemberListOutput> command = new Command<ZMemberListOutput>(ZRANGEBYSCORE, listener, new ZMemberListOutput(), 4);
        command.addArg(key).addArg(min).addArg(max).addArg(WITHSCORES);
        dispatch(command);
    }

    @Override
    public void zrangebyscoreWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, double min, double max, long offset, long count) {
        Command<ZMemberListOutput> command = new Command<ZMemberListOutput>(ZRANGEBYSCORE, listener, new ZMemberListOutput(), 7);
        command.addArg(key).addArg(min).addArg(max).addArg(WITHSCORES);
        command.addArg(LIMIT).addArg(offset).addArg(count);
        dispatch(command);
    }

    @Override
    public void zrank(CommandListener<LongOutput> listener, byte[] key, byte[] member) {
        Command<LongOutput> command = new Command<LongOutput>(ZRANK, listener, new LongOutput(), key, member);
        dispatch(command);
    }

    @Override
    public void zrem(CommandListener<LongOutput> listener, byte[] key, byte[]... members) {
        Command<LongOutput> command = new Command<LongOutput>(ZREM, listener, new LongOutput(), 1 + members.length);
        command.addArg(key).addArgs(members);
        dispatch(command);
    }

    @Override
    public void zremrangebyrank(CommandListener<LongOutput> listener, byte[] key, long start, long stop) {
        Command<LongOutput> command = new Command<LongOutput>(ZREMRANGEBYRANK, listener, new LongOutput(), 3);
        command.addArg(key).addArg(start).addArg(stop);
        dispatch(command);
    }

    @Override
    public void zremrangebyscore(CommandListener<LongOutput> listener, byte[] key, double min, double max) {
        Command<LongOutput> command = new Command<LongOutput>(ZREMRANGEBYSCORE, listener, new LongOutput(), 3);
        command.addArg(key).addArg(min).addArg(max);
        dispatch(command);
    }

    @Override
    public void zrevrange(CommandListener<ListOutput> listener, byte[] key, long start, long stop) {
        Command<ListOutput> command = new Command<ListOutput>(ZREVRANGE, listener, new ListOutput(), 3);
        command.addArg(key).addArg(start).addArg(stop);
        dispatch(command);
    }

    @Override
    public void zrevrangeWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, long start, long stop) {
        Command<ZMemberListOutput> command = new Command<ZMemberListOutput>(ZREVRANGE, listener, new ZMemberListOutput(), 4);
        command.addArg(key).addArg(start).addArg(stop).addArg(WITHSCORES);
        dispatch(command);
    }

    @Override
    public void zrevrangebyscore(CommandListener<ListOutput> listener, byte[] key, double min, double max) {
        Command<ListOutput> command = new Command<ListOutput>(ZREVRANGEBYSCORE, listener, new ListOutput(), 3);
        command.addArg(key).addArg(min).addArg(max);
        dispatch(command);
    }

    @Override
    public void zrevrangebyscore(CommandListener<ListOutput> listener, byte[] key, double min, double max, long offset, long count) {
        Command<ListOutput> command = new Command<ListOutput>(ZREVRANGEBYSCORE, listener, new ListOutput(), 6);
        command.addArg(key).addArg(min).addArg(max);
        command.addArg(LIMIT).addArg(offset).addArg(count);
        dispatch(command);
    }

    @Override
    public void zrevrangebyscoreWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, double min, double max) {
        Command<ZMemberListOutput> command = new Command<ZMemberListOutput>(ZREVRANGEBYSCORE, listener, new ZMemberListOutput(), 4);
        command.addArg(key).addArg(min).addArg(max).addArg(WITHSCORES);
        dispatch(command);
    }

    @Override
    public void zrevrangebyscoreWithScores(CommandListener<ZMemberListOutput> listener, byte[] key, double min, double max, long offset, long count) {
        Command<ZMemberListOutput> command = new Command<ZMemberListOutput>(ZREVRANGEBYSCORE, listener, new ZMemberListOutput(), 7);
        command.addArg(key).addArg(min).addArg(max).addArg(WITHSCORES);
        command.addArg(LIMIT).addArg(offset).addArg(count);
        dispatch(command);
    }

    @Override
    public void zrevrank(CommandListener<LongOutput> listener, byte[] key, byte[] member) {
        Command<LongOutput> command = new Command<LongOutput>(ZREVRANK, listener, new LongOutput(), key, member);
        dispatch(command);
    }

    @Override
    public void zscore(CommandListener<DoubleOutput> listener, byte[] key, byte[] member) {
        Command<DoubleOutput> command = new Command<DoubleOutput>(ZSCORE, listener, new DoubleOutput(), key, member);
        dispatch(command);
    }
}
