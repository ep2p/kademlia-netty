package io.ep2p.kademlia.netty;


import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.exception.DuplicateStoreRequest;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.netty.client.NettyMessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.node.DHTKademliaNode;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.DefaultRoutingTableFactory;
import io.ep2p.kademlia.table.RoutingTableFactory;
import io.ep2p.kademlia.util.BoundedHashUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DHTTestCase {
    private static KeyHashGenerator<BigInteger, String> keyHashGenerator;
    private static RoutingTableFactory<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTableFactory;

    private static NettyMessageSender nettyMessageSender1;
    private static NettyMessageSender nettyMessageSender2;
    private static NettyKadmliaDHTNode<BigInteger, String, String> node1;
    private static NettyKadmliaDHTNode<BigInteger, String, String> node2;


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

        nettyMessageSender1 = new NettyMessageSender<BigInteger>();
        nettyMessageSender2 = new NettyMessageSender<BigInteger>();

        // node 1
        NettyConnectionInfo nettyConnectionInfo = new NettyConnectionInfo("localhost", 8000);
        BigInteger id1 = BigInteger.valueOf(1);
        SampleRepository node1Repository = new SampleRepository();
        DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, String, String> kademliaNode = new DHTKademliaNode(
                id1,
                nettyConnectionInfo,
                routingTableFactory.getRoutingTable(id1),
                nettyMessageSender1,
                nodeSettings, node1Repository, keyHashGenerator
        );
        KademliaNodeServer<BigInteger, String, String> kademliaNodeServer = new KademliaNodeServer<>("localhost", 8000);
        node1 = new NettyKadmliaDHTNode<>(kademliaNode, kademliaNodeServer);
        node1.start();


        Thread.sleep(1000);

        // node 2
        NettyConnectionInfo nettyConnectionInfo2 = new NettyConnectionInfo("localhost", 8001);
        BigInteger id2 = BigInteger.valueOf(2);
        SampleRepository node2Repository = new SampleRepository();
        DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, String, String> kademliaNode2 = new DHTKademliaNode(
                id2,
                nettyConnectionInfo2,
                routingTableFactory.getRoutingTable(id2),
                nettyMessageSender2,
                nodeSettings, node2Repository, keyHashGenerator
        );
        KademliaNodeServer<BigInteger, String, String> kademliaNodeServer2 = new KademliaNodeServer<>("localhost", 8001);
        node2 = new NettyKadmliaDHTNode<>(kademliaNode2, kademliaNodeServer2);
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
