[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.ep2p/kademlia-netty/badge.png?gav=true)](https://maven-badges.herokuapp.com/maven-central/io.ep2p/kademlia-netty)
[![Github Releases](https://badgen.net/github/release/ep2p/kademlia-netty)](https://github.com/ep2p/kademlia-netty/releases)
[![Open Issues](https://badgen.net/github/open-issues/ep2p/kademlia-netty)](https://github.com/ep2p/kademlia-netty/issues)
[![Liscence](https://badgen.net/github/license/ep2p/kademlia-netty)](https://github.com/ep2p/kademlia-netty/blob/main/LICENSE)

# kademlia netty
**Status**: [ready to use]. [active maintainance].
Playground (sample runnable project) [available here](https://github.com/ep2p/kademlia-netty-standalone-test).

---

Implementation of [kademlia API](https://github.com/ep2p/kademlia-api) DHT using:

- Netty (as HTTP/1.1 server for each node, including [`ConnectionInfo`](https://github.com/ep2p/kademlia-api#connectioninfo) with `host` and `port`)
- OKHttp (as [`RequestSender`](https://github.com/ep2p/kademlia-api#messagesender-interface) implementation)
- ([Kademlia Gson Serialization](https://github.com/ep2p/kademlia-serialization-gson))

This library uses `BigInteger` as Node IDs and `NettyConnectionInfo` as implementation of `ConnectionInfo` interafce.

However, it is still abstract, therefore you should implement some parts that are mentioned in [kademlia API](https://github.com/ep2p/kademlia-api) such as:

- [DHT Repository](https://github.com/ep2p/kademlia-api#dht)
- Mechanism to persist and reload [Routing Table](https://github.com/ep2p/kademlia-api#routingtable)
- You still need to configure [`NodeSettings`](https://github.com/ep2p/kademlia-api#configuration). Default one may not be suitable for you.
- You need to implement [`KeyHasGenerator`](https://github.com/ep2p/kademlia-api/blob/main/src/main/java/io/ep2p/kademlia/node/KeyHashGenerator.java).
    This is used for bounding the key sizes to network size before storing messages.
  



---

## Examples

There are few working examples in tests directory.

[Main example](https://github.com/ep2p/kademlia-netty/blob/main/src/test/java/io/ep2p/kademlia/netty/examples/Example.java) 
has everything for you to start understanding how this library works. It creates 2 nodes and stores data in DHT.

[Custom DTO Example](https://github.com/ep2p/kademlia-netty/blob/main/src/test/java/io/ep2p/kademlia/netty/examples/CustomDTOSerialization.java)
is on top of previous example, but this time we are serializing and storing a different DTO object called `Person` instead of a simple String.


---

## Installation

Using maven:

```xml
<dependency>
    <groupId>io.ep2p</groupId>
    <artifactId>kademlia-netty</artifactId>
    <version>0.3.2-RELEASE</version>
</dependency>
```


---

## Donations

Coffee has a cost :smile:

Any  sort of small or large donations can be a motivation in maintaining this repository and related repositories.

- **ETH**: `0x5F120228C12e2C6923AfDeb0e811d74160166d90`
- **TRC20**: `TJjw5n26KFBqkJQbs7eKdxkVuk4pvJdFzE`
- **BTC**: `bc1qmtewrl7srjrkl8t4z5vantuqkz086srj4clzh3`


Cheers
