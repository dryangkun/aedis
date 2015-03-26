# aedis
async redis client on netty

# Features
- jdk1.6+
- high perfomance
- asynchronous
- thread safe

# Basic Usage
one channel to one server
```java
EventLoopGroup group = new NioEventLoopGroup();
AedisBootstrap bootstrap = new AedisBootstrap(group);
bootstrap.setHostAndPort("localhost", 6379);

//Aedis is Thread-safe
Aedis aedis = bootstrap.newAedis();
aedis.set(new CommandListener<StatusOutput>() {
    @Override
    public void operationComplete(Command<StatusOutput> command) {
        System.out.println(command);
        if (command.isOutputSuccess()) {
            System.out.println("result - " + command.get().ok());
        } else {
            System.out.println("cause - " + command.cause());
        }
    }
}, "abc".getBytes(), "xxoo".getBytes());

final CountDownLatch latch = new CountDownLatch(1);
aedis.get(new CommandListener<ByteArrayOutput>() {
    @Override
    public void operationComplete(Command<ByteArrayOutput> command) {
        System.out.println(command);
        latch.countDown();
    }
}, "abc".getBytes());
latch.await();

//close && stop
aedis.close();
group.shutdownGracefully();
```

# Group Usage
many channel to one server
```java
EventLoopGroup group = new NioEventLoopGroup();
AedisBootstrap bootstrap = new AedisBootstrap(group);
bootstrap.setHostAndPort("localhost", 6379);

AedisGroup aedis = bootstrap.newAedisGroup(80);
//same with Aedis
```

# Pipeline Usage
send multi commands with one flush operation
```java
Aedis aedis = bootstrap.newAedis();

Pipeline pipeline = aedis.pipeline();
ListenerCombiner combiner = new ListenerCombiner() {
    @Override
    protected void operationComplete(Map<Integer, Command> map) {
        System.out.println(map);
    }
};
pipeline.set(combiner.newListener(StatusOutput.class), "abc".getBytes(), "xxoo".getBytes());
pipeline.get(combiner.newListener(ByteArrayOutput.class), "abc".getBytes());
pipeline.syncCommands();
```

# Notice

## prevent out of memory
you can call AedisBootstrap.setCommand_queue_capacity to the write queue size
and the read queue size. if write too much and redis server response to slow,
then the command listener will call with cause in queuefull(Command.isQueuefullFailure() == true)

## more options to netty Bootstrap
you can call AedisBootstrap.getBootstrap() to get the instance, and then set/add options
