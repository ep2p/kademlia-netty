package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.exception.UnsupportedBoundingException;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.netty.builder.NettyKademliaDHTNodeBuilder;
import io.ep2p.kademlia.netty.client.OkHttpMessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.util.BoundedHashUtil;
import lombok.SneakyThrows;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class Example {

    @SneakyThrows
    public static void main(String[] args) {
        NodeSettings.Default.IDENTIFIER_SIZE = 4;
        NodeSettings.Default.BUCKET_SIZE = 100;
        NodeSettings.Default.PING_SCHEDULE_TIME_VALUE = 5;

        OkHttpMessageSender<String, String> okHttpMessageSender = new OkHttpMessageSender<>();

        KeyHashGenerator<BigInteger, String> keyHashGenerator = key -> {
            try {
                return new BoundedHashUtil(NodeSettings.Default.IDENTIFIER_SIZE).hash(key.hashCode(), BigInteger.class);
            } catch (UnsupportedBoundingException e) {
                e.printStackTrace();
            }
            return BigInteger.valueOf(key.hashCode());
        };


        NettyKademliaDHTNode<String, String> node1 = new NettyKademliaDHTNodeBuilder<>(
                BigInteger.valueOf(2L),
                new NettyConnectionInfo("127.0.0.1", 8000),
                new SampleRepository(),
                keyHashGenerator
        ).build();
        node1.start();


        // node 2
        NettyKademliaDHTNode< String, String> node2 = new NettyKademliaDHTNodeBuilder<>(
                BigInteger.valueOf(2L),
                new NettyConnectionInfo("127.0.0.1", 8001),
                new SampleRepository(),
                keyHashGenerator
        ).build();

        System.out.println("Bootstrapped? " + node2.start(node1).get(5, TimeUnit.SECONDS));

        StoreAnswer<BigInteger, String> storeAnswer = node2.store("K", "V").get();
        System.out.println(storeAnswer.getResult());
        System.out.println(storeAnswer.getNodeId());

        LookupAnswer<BigInteger, String, String> k = node1.lookup("K").get();
        System.out.println(k.getResult());
        System.out.println(k.getValue());

        node1.stopNow();
        node2.stopNow();
        okHttpMessageSender.stop();

        System.exit(0);
    }

}
