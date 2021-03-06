package io.ep2p.kademlia.netty.builder;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.netty.client.NettyMessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.netty.factory.KademliaMessageHandlerFactory;
import io.ep2p.kademlia.netty.factory.NettyServerInitializerFactory;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.netty.server.filter.KademliaMainHandlerFilter;
import io.ep2p.kademlia.netty.server.filter.NettyKademliaServerFilterChain;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.DefaultRoutingTableFactory;
import io.ep2p.kademlia.table.RoutingTableFactory;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/* This class can later be improved to make default values of NettyKademliaDHTNodeBuilder more dynamically */
public class NettyKademliaDHTNodeDefaults {

    private interface DefaultFillerPipeline<K extends Serializable, V extends Serializable> {
        void process(NettyKademliaDHTNodeBuilder<K, V> builder);
    }

    public static <K extends Serializable, V extends Serializable> void run(NettyKademliaDHTNodeBuilder<K, V> builder){
        List<DefaultFillerPipeline<K, V>> pipeline = getPipeline();
        pipeline.forEach(pipe -> {
            pipe.process(builder);
        });
    }

    private static <K extends Serializable, V extends Serializable> List<DefaultFillerPipeline<K, V>> getPipeline(){
        List<DefaultFillerPipeline<K, V>> pipelines = new ArrayList<>();

        pipelines.add(new DefaultFillerPipeline<K, V>() {
            @Override
            public void process(NettyKademliaDHTNodeBuilder<K, V> builder) {
                if (builder.getNodeSettings() == null) {
                    builder.nodeSettings(NodeSettings.Default.build());
                }
            }
        });

        pipelines.add(new DefaultFillerPipeline<K, V>() {
            @Override
            public void process(NettyKademliaDHTNodeBuilder<K, V> builder) {
                if (builder.getRoutingTable() == null) {
                    RoutingTableFactory<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTableFactory = new DefaultRoutingTableFactory<>(builder.getNodeSettings());
                    builder.routingTable(routingTableFactory.getRoutingTable(builder.getId()));
                }
            }
        });

        pipelines.add(new DefaultFillerPipeline<K, V>() {
            @Override
            public void process(NettyKademliaDHTNodeBuilder<K, V> builder) {
                if (builder.getGsonFactory() == null) {
                    builder.gsonFactory(new GsonFactory.DefaultGsonFactory<>());
                }
            }
        });

        pipelines.add(new DefaultFillerPipeline<K, V>() {
            @Override
            public void process(NettyKademliaDHTNodeBuilder<K, V> builder) {
                if (builder.getMessageSender() == null) {
                    builder.messageSender( new NettyMessageSender<K, V>(builder.getGsonFactory().gson()));
                }
            }
        });

        pipelines.add(new DefaultFillerPipeline<K, V>() {
            @Override
            public void process(NettyKademliaDHTNodeBuilder<K, V> builder) {
                if (builder.getKademliaMessageHandlerFactory() == null) {
                    NettyKademliaServerFilterChain<K, V> filterChain = new NettyKademliaServerFilterChain<>();
                    filterChain.addFilter(new KademliaMainHandlerFilter<>(builder.getGsonFactory().gson()));
                    builder.kademliaMessageHandlerFactory(new KademliaMessageHandlerFactory.DefaultKademliaMessageHandlerFactory<>(filterChain));
                }
            }
        });

        pipelines.add(new DefaultFillerPipeline<K, V>() {
            @Override
            public void process(NettyKademliaDHTNodeBuilder<K, V> builder) {
                if (builder.getNettyServerInitializerFactory() == null) {
                    builder.nettyServerInitializerFactory(
                            new NettyServerInitializerFactory.DefaultNettyServerInitializerFactory<>(
                                    builder.getKademliaMessageHandlerFactory()
                            )
                    );
                }
            }
        });

        pipelines.add(new DefaultFillerPipeline<K, V>() {
            @Override
            public void process(NettyKademliaDHTNodeBuilder<K, V> builder) {
                if (builder.getKademliaNodeServer() == null){
                    builder.kademliaNodeServer(
                            new KademliaNodeServer<>(
                                    builder.getConnectionInfo().getHost(),
                                    builder.getConnectionInfo().getPort(),
                                    builder.getNettyServerInitializerFactory()
                            )
                    );
                }
            }
        });


        return pipelines;
    }

}
