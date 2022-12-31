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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DHTTest {

    private static NettyKademliaDHTNode<String, String> node1;
    private static NettyKademliaDHTNode<String, String> node2;


    @SneakyThrows
    @BeforeAll
    public static void init() {
        NodeSettings.Default.IDENTIFIER_SIZE = 4;
        NodeSettings.Default.BUCKET_SIZE = 100;
        NodeSettings.Default.PING_SCHEDULE_TIME_VALUE = 100;
//        NodeSettings.Default.ENABLED_FIRST_STORE_REQUEST_FORCE_PASS = false;

        KeyHashGenerator<BigInteger, String> keyHashGenerator = key -> {
            try {
                return new BoundedHashUtil(NodeSettings.Default.IDENTIFIER_SIZE).hash(key.hashCode(), BigInteger.class);
            } catch (UnsupportedBoundingException e) {
                e.printStackTrace();
            }
            return BigInteger.valueOf(key.hashCode());
        };

        // node 1
        node1 = new NettyKademliaDHTNodeBuilder<>(
                BigInteger.valueOf(1L),
                new NettyConnectionInfo("127.0.0.1", NodeHelper.findRandomPort()),
                new SampleRepository(),
                keyHashGenerator,
                String.class, String.class).build();
        node1.start();


        // node 2
        node2 = new NettyKademliaDHTNodeBuilder<>(
                BigInteger.valueOf(2L),
                new NettyConnectionInfo("127.0.0.1", NodeHelper.findRandomPort()),
                new SampleRepository(),
                keyHashGenerator,
                String.class, String.class).build();
        System.out.println("Bootstrapped? " + node2.start(node1).get(5, TimeUnit.SECONDS));

    }

    @AfterAll
    public static void cleanup(){
        node1.stopNow();
        node2.stopNow();
    }

    @Test
    void testDhtStoreLookup() throws  ExecutionException, InterruptedException {
        String[] values = new String[]{"V", "ABC", "SOME VALUE"};
        for (String v : values){
            System.out.println("Testing DHT for K: " + v.hashCode() + " & V: " + v);
            StoreAnswer<BigInteger, NettyConnectionInfo, String> storeAnswer = node2.store("" + v.hashCode(), v).get();
            Assertions.assertEquals(StoreAnswer.Result.STORED, storeAnswer.getResult());
            System.out.println(storeAnswer.getNode().getId() + " stored data");
            LookupAnswer<BigInteger, NettyConnectionInfo, String, String> lookupAnswer = node1.lookup("" + v.hashCode()).get();
            System.out.println("Node " + node1.getId() + " found " + v.hashCode() + " from " + lookupAnswer.getNode().getId());
            Assertions.assertEquals(LookupAnswer.Result.FOUND, lookupAnswer.getResult());
            Assertions.assertEquals(lookupAnswer.getValue(), v);

            lookupAnswer = node2.lookup("" + v.hashCode()).get();
            Assertions.assertEquals(LookupAnswer.Result.FOUND, lookupAnswer.getResult());
            Assertions.assertNotNull(lookupAnswer.getValue());
            Assertions.assertEquals(v, lookupAnswer.getValue());
            System.out.println("Node " + node2.getId() + " found " + v.hashCode() + " from " + lookupAnswer.getNode().getId());
        }

    }

    @Test
    void testNetworkKnowledge(){
        Assertions.assertTrue(node1.getRoutingTable().contains(BigInteger.valueOf(2L)));
        Assertions.assertTrue(node2.getRoutingTable().contains(BigInteger.valueOf(1L)));
    }

}
