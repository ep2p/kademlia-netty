[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.ep2p/kademlia-netty/badge.png?gav=true)](https://maven-badges.herokuapp.com/maven-central/io.ep2p/kademlia-netty)
[![Github Releases](https://badgen.net/github/release/ep2p/kademlia-netty)](https://github.com/ep2p/kademlia-netty/releases)
[![Open Issues](https://badgen.net/github/open-issues/ep2p/kademlia-netty)](https://github.com/ep2p/kademlia-netty/issues)
[![Liscence](https://badgen.net/github/license/ep2p/kademlia-netty)](https://github.com/ep2p/kademlia-netty/blob/main/LICENSE)

# kademlia netty

**This Project is still under development** 

Implementation of [kademlia API](https://github.com/ep2p/kademlia-api) DHT using:

- Netty (as server for each node, including [`ConnectionInfo`](https://github.com/ep2p/kademlia-api#connectioninfo))
- OKHttp (as [`RequestSender`](https://github.com/ep2p/kademlia-api#messagesender-interface) implementation)
- Gson (as serializer/deserializer)

This library uses `BigInteger` as Node IDs to be able to cover all size of numbers.
However, it is still abstract, therefore you should implement some parts that are mentioned in [kademlia API](https://github.com/ep2p/kademlia-api) such as:

- [DHT Repository](https://github.com/ep2p/kademlia-api#dht)
- Mechanism to persist and reload [Routing Table](https://github.com/ep2p/kademlia-api#routingtable)
- You still need to configure [`NodeSettings`](https://github.com/ep2p/kademlia-api#configuration). Default one may not be suitable for you.
- You need to implement [`KeyHasGenerator`](https://github.com/ep2p/kademlia-api/blob/main/src/main/java/io/ep2p/kademlia/node/KeyHashGenerator.java).
    This is used for bounding the key sizes to network size before storing messages.
  



---

## Examples

In this section you can see some examples on how to use this library just to test it.

### Basic

If you are not fan of making many changes, here is a place to start. Let's set up a very basic node.


First lets get some things ready:

```java


// Implement your KeyHashGenerator. here is a basic one:

KeyHashGenerator<BigInteger, String> keyHashGenerator = new KeyHashGenerator<BigInteger, String>() {
    @Override
    public BigInteger generateHash(String key) {
        //io.ep2p.kademlia.util.BoundedHashUtil
        return new BoundedHashUtil(NodeSettings.Default.IDENTIFIER_SIZE).hash(key.hashCode(), BigInteger.class);
    }
};


// Implement your DHT repository

KademliaRepository<String, String> repository = new KademliaRepository<String, String>() {
    private final Map<String, String> data = new HashMap<>();

    @Override
    public void store(String key, String value) {
        data.putIfAbsent(key, value);
    }

    @Override
    public String get(String key) {
        return data.get(key);
    }

    @Override
    public void remove(String key) {
        data.remove(key);
    }

    @Override
    public boolean contains(String key) {
        return data.containsKey(key);
    }
};

```


Now that we have our DHT repository ready, it's time to create a basic node. Lets run it on `127.0.0.1:8000`

```java

// run our node on 127.0.0.1 : 8000

NettyKadmliaDHTNode<String, String> node1 = new NettyKademliaDHTNodeBuilder<String, String>()
                .id(BigInteger.valueOf(1))
                .connectionInfo(new NettyConnectionInfo("127.0.0.1", 8000))
                .nodeSettings(nodeSettings) // remove if not overriding node settings
                .keyHashGenerator(keyHashGenerator)
                .repository(repository)
                .build();


// we can start our node right now:

node1.start()
```

Let's run a separate node on a different machine/port. We assume it's running on `127.0.0.1:8001`.

```java

NettyKadmliaDHTNode<String, String> node2 = ...;

// instead of running it, bootstrap it with first node like:

// init bootstrap node. use information from node 1
Node<BigInteger, NettyConnectionInfo> bootstrapNode = new NettyBigIntegerExternalNode(new NettyConnectionInfo("127.0.0.1", 8000), BigInteger.valueOf(1), new Date());

node2.start(bootstrapNode)

```

Now its time to test DHT:

```java
StoreAnswer<BigInteger, String> storeAnswer = node2.store("Key", "Your Value Here").get();
System.out.println(storeAnswer.getResult());
System.out.println(storeAnswer.getNodeId());

LookupAnswer<BigInteger, String, String> k = node1.lookup("K").get();
System.out.println(k.getResult());
System.out.println(k.getValue());
```

Depending on the actual `Key`, `Value` and `NodeID`s, data will be persisted on different nodes.

We can now shutdown our nodes
```
// gracefully:
node1.stop()

//or
node1.stopNow()
```

---

## Installation

Using maven:

```xml
<dependency>
    <groupId>io.ep2p</groupId>
    <artifactId>kademlia-netty</artifactId>
    <version>0.1.0-RELEASE</version>
</dependency>
```


Cheers
