package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.exception.UnsupportedBoundingException;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.netty.builder.NettyKademliaDHTNodeBuilder;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.util.BoundedHashUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class DHTNodeShutdownTest {

    @SneakyThrows
    @Test
    public void testFailLookupAfterShutdown() {
        // Setting NodeSettings
        NodeSettings.Default.IDENTIFIER_SIZE = 3;
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
                keyHashGenerator
        ).build();
        node1.start();

        NettyKademliaDHTNode<String, String> node2 = new NettyKademliaDHTNodeBuilder<>(
                BigInteger.valueOf(2L),
                new NettyConnectionInfo("127.0.0.1", 8001),
                new SampleRepository(),
                keyHashGenerator
        ).build();
        node2.start(node1).get();

        Thread.sleep(2000);
        StoreAnswer<BigInteger, String> storeAnswer = node1.store("1", "1").get();
        Assertions.assertEquals(StoreAnswer.Result.STORED, storeAnswer.getResult());

        LookupAnswer<BigInteger, String, String> lookupAnswer = node1.lookup("1").get();
        System.out.printf("A Lookup result: %s - Value: %s%n", lookupAnswer.getResult(), lookupAnswer.getValue());
        Assertions.assertEquals(LookupAnswer.Result.FOUND, lookupAnswer.getResult());

        lookupAnswer = node2.lookup("1").get();
        System.out.printf("B Lookup result: %s - Value: %s%n", lookupAnswer.getResult(), lookupAnswer.getValue());
        Assertions.assertEquals(LookupAnswer.Result.FOUND, lookupAnswer.getResult());

        node1.stopNow();

        lookupAnswer = node2.lookup("1").get();
        Assertions.assertEquals(LookupAnswer.Result.FAILED, lookupAnswer.getResult());

        node2.stopNow();

    }

}
