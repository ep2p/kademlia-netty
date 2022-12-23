package io.ep2p.kademlia.netty.examples;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.exception.UnsupportedBoundingException;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.netty.NettyKademliaDHTNode;
import io.ep2p.kademlia.netty.SampleRepository;
import io.ep2p.kademlia.netty.builder.NettyKademliaDHTNodeBuilder;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.util.BoundedHashUtil;
import lombok.SneakyThrows;

import java.math.BigInteger;

public class Example {

    @SneakyThrows
    public static void main(String[] args) {
        // Setting NodeSettings
        NodeSettings.Default.IDENTIFIER_SIZE = 4;
        NodeSettings.Default.BUCKET_SIZE = 100;   // K=100 for k-buckets
        NodeSettings.Default.PING_SCHEDULE_TIME_VALUE = 5;  // Ping every 5 seconds (doesn't matter in our case)


        // Determines hash of a key in DHT. Check kademlia-api
        KeyHashGenerator<BigInteger, String> keyHashGenerator = key -> {
            try {
                return new BoundedHashUtil(NodeSettings.Default.IDENTIFIER_SIZE).hash(key.hashCode(), BigInteger.class);
            } catch (UnsupportedBoundingException e) {
                e.printStackTrace();
            }
            return BigInteger.valueOf(key.hashCode());
        };


        // Starting node 1
        NettyKademliaDHTNode<String, String> node1 = new NettyKademliaDHTNodeBuilder<>(
                BigInteger.valueOf(1L),
                new NettyConnectionInfo("127.0.0.1", 8000),
                new SampleRepository(),
                keyHashGenerator,
                String.class, String.class).build();
        node1.start();


        // Starting node 2 - bootstrapping with node 1
        NettyKademliaDHTNode< String, String> node2 = new NettyKademliaDHTNodeBuilder<>(
                BigInteger.valueOf(2L),
                new NettyConnectionInfo("127.0.0.1", 8001),
                new SampleRepository(),
                keyHashGenerator,
                String.class, String.class).build();
        node2.start(node1).get();  // Wait till bootstrap future finishes


        // Store a value in DHT through node 1 (in this case, a key with name "K" will definitely get stored in node 2)
        StoreAnswer<BigInteger, NettyConnectionInfo, String> storeAnswer = node2.store("K", "V").get();
        System.out.printf("Store result: %s - Node: %s%n", storeAnswer.getResult(), storeAnswer.getNode().getId());
        System.out.printf("Data in node 2: %s%n", node2.getKademliaRepository().get("K"));

        // Retrieve the value of key "K" through node 1
        LookupAnswer<BigInteger, NettyConnectionInfo, String, String> lookupAnswer = node1.lookup("K").get();
        System.out.printf("Lookup result: %s - Value: %s%n", lookupAnswer.getResult(), lookupAnswer.getValue());

        for (int i = 0; i < 100; i++){
            System.out.println("Looking up");
            lookupAnswer = node2.lookup("1").get();
            System.out.printf("Lookup result: %s - Value: %s%n", lookupAnswer.getResult(), lookupAnswer.getValue());
            Thread.sleep(500);
        }
        node1.stopNow();
        node2.stopNow();

        System.exit(0);
    }

}
