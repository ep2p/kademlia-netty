package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.NodeSettings;
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

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Example {

    @SneakyThrows
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        NodeSettings.Default.IDENTIFIER_SIZE = 4;
        NodeSettings.Default.BUCKET_SIZE = 100;
        NodeSettings.Default.PING_SCHEDULE_TIME_VALUE = 5;
        NodeSettings nodeSettings = NodeSettings.Default.build();

        NettyMessageSender nettyMessageSender = new NettyMessageSender();

        RoutingTableFactory<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTableFactory = new DefaultRoutingTableFactory<>(nodeSettings);
        KeyHashGenerator<BigInteger, String> keyHashGenerator = new KeyHashGenerator<BigInteger, String>() {
            @Override
            public BigInteger generateHash(String key) {
                return new BoundedHashUtil(NodeSettings.Default.IDENTIFIER_SIZE).hash(key.hashCode(), BigInteger.class);
            }
        };


        // node 1
        NettyConnectionInfo nettyConnectionInfo = new NettyConnectionInfo("localhost", 8000);
        BigInteger id1 = BigInteger.valueOf(1);
        SampleRepository node1Repository = new SampleRepository();
        DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, String, String> kademliaNode = new DHTKademliaNode(
                id1,
                nettyConnectionInfo,
                routingTableFactory.getRoutingTable(id1),
                nettyMessageSender,
                nodeSettings, node1Repository, keyHashGenerator
        );
        KademliaNodeServer<String, String> kademliaNodeServer = new KademliaNodeServer<>("localhost", 8000);
        NettyKadmliaDHTNode<String, String> node1 = new NettyKadmliaDHTNode<>(kademliaNode, kademliaNodeServer);
        node1.start();

        Thread.sleep(2000);

        // node 2
        NettyConnectionInfo nettyConnectionInfo2 = new NettyConnectionInfo("localhost", 8001);
        BigInteger id2 = BigInteger.valueOf(2);
        SampleRepository node2Repository = new SampleRepository();
        DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, String, String> kademliaNode2 = new DHTKademliaNode(
                id2,
                nettyConnectionInfo2,
                routingTableFactory.getRoutingTable(id2),
                nettyMessageSender,
                nodeSettings, node2Repository, keyHashGenerator
        );
        KademliaNodeServer<String, String> kademliaNodeServer2 = new KademliaNodeServer<>("localhost", 8001);
        NettyKadmliaDHTNode<String, String> node2 = new NettyKadmliaDHTNode<>(kademliaNode2, kademliaNodeServer2);
        System.out.println("Bootstrapped? " + node2.start(node1).get(5, TimeUnit.SECONDS));

        StoreAnswer<BigInteger, String> storeAnswer = node2.store("K", "V").get();
        System.out.println(storeAnswer.getResult());
        System.out.println(storeAnswer.getNodeId());

        LookupAnswer<BigInteger, String, String> k = node1.lookup("K").get();
        System.out.println(k.getResult());
        System.out.println(k.getValue());

        node1.stopNow();
        node2.stopNow();
        nettyMessageSender.stop();

        System.exit(0);
    }

}
