package io.ep2p.kademlia.netty;


import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.exception.DuplicateStoreRequest;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.netty.client.NettyMessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.DefaultRoutingTableFactory;
import io.ep2p.kademlia.table.RoutingTableFactory;
import io.ep2p.kademlia.util.BoundedHashUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DHTTestCase {
    private static KeyHashGenerator<BigInteger, String> keyHashGenerator;
    private static RoutingTableFactory<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTableFactory;

    private static NettyMessageSender nettyMessageSender1;
    private static NettyMessageSender nettyMessageSender2;
    private static NettyKadmliaDHTNode<String, String> node1;
    private static NettyKadmliaDHTNode<String, String> node2;


    @SneakyThrows
    @BeforeAll
    public static void init() throws InterruptedException {
        NodeSettings.Default.IDENTIFIER_SIZE = 4;
        NodeSettings.Default.BUCKET_SIZE = 100;
        NodeSettings.Default.PING_SCHEDULE_TIME_VALUE = 5;
        NodeSettings nodeSettings = NodeSettings.Default.build();

        routingTableFactory = new DefaultRoutingTableFactory<>(nodeSettings);
        keyHashGenerator = new KeyHashGenerator<BigInteger, String>() {
            @Override
            public BigInteger generateHash(String key) {
                return new BoundedHashUtil(NodeSettings.Default.IDENTIFIER_SIZE).hash(key.hashCode(), BigInteger.class);
            }
        };

        nettyMessageSender1 = new NettyMessageSender();
        nettyMessageSender2 = new NettyMessageSender();

        // node 1
        node1 = new NettyKademliaDHTNodeBuilder<String, String>()
                .id(BigInteger.valueOf(1))
                .connectionInfo(new NettyConnectionInfo("127.0.0.1", 8000))
                .nodeSettings(nodeSettings)
                .keyHashGenerator(keyHashGenerator)
                .repository(new SampleRepository())
                .build();
        node1.start();


        Thread.sleep(1000);

        // node 2
        node2 = new NettyKademliaDHTNodeBuilder<String, String>()
                .id(BigInteger.valueOf(2))
                .connectionInfo(new NettyConnectionInfo("127.0.0.1", 8001))
                .nodeSettings(nodeSettings)
                .keyHashGenerator(keyHashGenerator)
                .repository(new SampleRepository())
                .build();
        System.out.println("Bootstrapped? " + node2.start(node1).get(5, TimeUnit.SECONDS));

        Thread.sleep(1000);
    }

    @AfterAll
    public static void cleanup(){
        nettyMessageSender1.stop();
        nettyMessageSender2.stop();
        node1.stop();
        node2.stop();
    }

    @Test
    public void testDhtStoreLookup() throws DuplicateStoreRequest, ExecutionException, InterruptedException {
        String[] values = new String[]{"V", "ABC", "SOME VALUE"};
        for (String v : values){
            System.out.println("Testing DHT for K: " + v.hashCode() + " & V: " + v);
            StoreAnswer<BigInteger, String> storeAnswer = node2.store("" + v.hashCode(), v).get();
            Assertions.assertEquals(storeAnswer.getResult(), StoreAnswer.Result.STORED);

            LookupAnswer<BigInteger, String, String> lookupAnswer = node1.lookup("" + v.hashCode()).get();
            Assertions.assertEquals(lookupAnswer.getResult(), LookupAnswer.Result.FOUND);
            Assertions.assertEquals(lookupAnswer.getValue(), v);
            System.out.println("Node " + node1.getId() + " found " + v.hashCode() + " from " + lookupAnswer.getNodeId());

            lookupAnswer = node2.lookup("" + v.hashCode()).get();
            Assertions.assertEquals(lookupAnswer.getResult(), LookupAnswer.Result.FOUND);
            Assertions.assertEquals(lookupAnswer.getValue(), v);
            System.out.println("Node " + node2.getId() + " found " + v.hashCode() + " from " + lookupAnswer.getNodeId());
        }

    }

    @Test
    public void testNetworkKnowledge(){
        Assertions.assertTrue(node1.getRoutingTable().contains(BigInteger.valueOf(2)));
        Assertions.assertTrue(node2.getRoutingTable().contains(BigInteger.valueOf(1)));
    }

}
