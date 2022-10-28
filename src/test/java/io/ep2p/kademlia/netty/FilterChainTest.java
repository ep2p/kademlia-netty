package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.exception.UnsupportedBoundingException;
import io.ep2p.kademlia.netty.builder.NettyKademliaDHTNodeBuilder;
import io.ep2p.kademlia.netty.client.OkHttpMessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.netty.factory.KademliaMessageHandlerFactory;
import io.ep2p.kademlia.netty.serialization.GsonMessageSerializer;
import io.ep2p.kademlia.netty.server.filter.KademliaMainHandlerFilter;
import io.ep2p.kademlia.netty.server.filter.NettyKademliaServerFilter;
import io.ep2p.kademlia.netty.server.filter.NettyKademliaServerFilterChain;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.util.BoundedHashUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.mock;

public class FilterChainTest {
    private static OkHttpMessageSender<String, String> okHttpMessageSender1;
    private static NettyKademliaDHTNode<String, String> node1;

    private static class EmptyFilter extends NettyKademliaServerFilter<String, String>{}

    @SneakyThrows
    @BeforeAll
    public static void init() {
        NodeSettings.Default.IDENTIFIER_SIZE = 4;
        NodeSettings.Default.BUCKET_SIZE = 100;
        NodeSettings.Default.PING_SCHEDULE_TIME_VALUE = 5;

        KeyHashGenerator<Long, String> keyHashGenerator = key -> {
            try {
                return new BoundedHashUtil(NodeSettings.Default.IDENTIFIER_SIZE).hash(key.hashCode(), Long.class);
            } catch (UnsupportedBoundingException e) {
                e.printStackTrace();
            }
            return Long.valueOf(key.hashCode());
        };

        okHttpMessageSender1 = new OkHttpMessageSender<>();

        // node 1
        node1 = new NettyKademliaDHTNodeBuilder<>(
                1L,
                new NettyConnectionInfo("127.0.0.1", NodeHelper.findRandomPort()),
                new SampleRepository(),
                keyHashGenerator
        ).build();
        node1.start();

    }

    @AfterAll
    public static void cleanup(){
        okHttpMessageSender1.stop();
        node1.stop();
    }

    @Test
    void testFilterChain() throws ExecutionException, InterruptedException, IOException, TimeoutException {
        @SuppressWarnings("unchecked")
        NettyKademliaServerFilter<String, String> mockFilter = mock(NettyKademliaServerFilter.class);

        NettyKademliaServerFilterChain<String, String> filterChain = new NettyKademliaServerFilterChain<>();
        filterChain.addFilter(new EmptyFilter());
        filterChain.addFilter(new KademliaMainHandlerFilter<>(new GsonMessageSerializer<>(new GsonFactory.DefaultGsonFactory<>().gsonBuilder())));
        filterChain.addFilterAfter(EmptyFilter.class, mockFilter);


        NettyKademliaDHTNode<String, String> node2 = new NettyKademliaDHTNodeBuilder<>(
                2L,
                new NettyConnectionInfo("127.0.0.1", NodeHelper.findRandomPort()),
                new SampleRepository(),
                node1.getKeyHashGenerator()
        )
                .kademliaMessageHandlerFactory(new KademliaMessageHandlerFactory.DefaultKademliaMessageHandlerFactory<>(filterChain))
                .build();
        System.out.println("Bootstrapped? " + node2.start(node1).get(5, TimeUnit.SECONDS));


        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            node2.stop();
            latch.countDown();
        }).start();

        Assertions.assertTrue(filterChain.getFilters().get(0) instanceof EmptyFilter);
        Assertions.assertFalse(filterChain.getFilters().get(1) instanceof KademliaMainHandlerFilter);

        latch.await();
    }
}
