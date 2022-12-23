package io.ep2p.kademlia.netty;


import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.netty.builder.NettyKademliaDHTNodeBuilder;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.KeyHashGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DHTTestRandom {

    private static KeyHashGenerator<BigInteger, String> keyHashGenerator;
    private static List<NettyKademliaDHTNode<String, String>> nodes = new ArrayList<>();


    @SneakyThrows
    @BeforeAll
    public static void init() {
        NodeSettings.Default.IDENTIFIER_SIZE = 128;
        NodeSettings.Default.BUCKET_SIZE = 100;
        NodeSettings.Default.PING_SCHEDULE_TIME_VALUE = 5;
        NodeSettings.Default.PING_SCHEDULE_TIME_UNIT = TimeUnit.SECONDS;
        keyHashGenerator = (k) -> BigInteger.valueOf(1);
    }

    @Test
    void testDHTStoreRandomKeys() throws IOException, ExecutionException, InterruptedException, TimeoutException {

        NettyKademliaDHTNode<String, String> previousNode = null;
        for (int i = 1; i < 16; i++){
            NettyKademliaDHTNode<String, String> nettyKademliaDHTNode = new NettyKademliaDHTNodeBuilder<>(
                    BigInteger.valueOf(new Random().nextInt((int) Math.pow(2, NodeSettings.Default.IDENTIFIER_SIZE))),
                    new NettyConnectionInfo("127.0.0.1", NodeHelper.findRandomPort()),
                    new SampleRepository(),
                    keyHashGenerator,
                    String.class, String.class).build();
            if (previousNode == null){
                nettyKademliaDHTNode.start();
            }else {
                Assertions.assertTrue(nettyKademliaDHTNode.start(previousNode).get(5, TimeUnit.SECONDS));
            }
            nodes.add(nettyKademliaDHTNode);
            System.out.println("Stored data in " + nettyKademliaDHTNode.getId());
            previousNode = nettyKademliaDHTNode;
        }

        System.out.println("Bootstrapped all nodes. Looking up for data");

        Thread.sleep(6000);
        nodes.forEach(kademliaDHTNode -> {
            try {
                Assertions.assertEquals(StoreAnswer.Result.STORED, kademliaDHTNode.store(kademliaDHTNode.getId().toString(), "data").get(5, TimeUnit.SECONDS).getResult());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        nodes.forEach(kademliaDHTNode -> {
            nodes.forEach(otherNode -> {
                try {
                    LookupAnswer<BigInteger, NettyConnectionInfo, String, String> lookupAnswer = kademliaDHTNode.lookup(otherNode.getId().toString()).get(10, TimeUnit.SECONDS);
                    Assertions.assertEquals(LookupAnswer.Result.FOUND, lookupAnswer.getResult(), kademliaDHTNode.getId() + " couldn't find key " + otherNode.getId());
                    System.out.println("Requester: " + kademliaDHTNode.getId() + " - Key: " + otherNode.getId() + " - Owner: " + lookupAnswer.getNode().getId());
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            });
        });

        System.out.println("Test passed successfully. Shutting down.");
        Thread.sleep(5000);
        nodes.forEach(kademliaDHTNode -> {
            kademliaDHTNode.stopNow();
        });

    }

}
