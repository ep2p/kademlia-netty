package io.ep2p.kademlia.netty.builder;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.netty.client.OkHttpMessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.NettyChannelInboundHandlerFactory;
import io.ep2p.kademlia.netty.factory.NettyChannelInitializerFactory;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.serialization.gson.GsonFactory;
import io.ep2p.kademlia.serialization.gson.GsonMessageSerializer;
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
        pipeline.forEach(pipe -> pipe.process(builder));
    }

    private static <K extends Serializable, V extends Serializable> List<DefaultFillerPipeline<K, V>> getPipeline(){
        List<DefaultFillerPipeline<K, V>> pipelines = new ArrayList<>();

        pipelines.add(builder -> {
            if (builder.getNodeSettings() == null) {
                builder.nodeSettings(NodeSettings.Default.build());
            }
        });

        pipelines.add(builder -> {
            if (builder.getRoutingTable() == null) {
                RoutingTableFactory<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTableFactory = new DefaultRoutingTableFactory<>(builder.getNodeSettings());
                builder.routingTable(routingTableFactory.getRoutingTable(builder.getId()));
            }
        });

        pipelines.add(builder -> {
            if (builder.getGsonFactory() == null) {
                builder.gsonFactory(new GsonFactory.DefaultGsonFactory<BigInteger, NettyConnectionInfo, K, V>(BigInteger.class, NettyConnectionInfo.class, builder.getKeyClass(), builder.getValueClass()));
            }
        });

        pipelines.add(builder -> {
            if (builder.getMessageSerializer() == null){
                builder.messageSerializer(new GsonMessageSerializer<BigInteger, NettyConnectionInfo, K, V>(builder.getGsonFactory().gsonBuilder()));
            }
        });

        pipelines.add(builder -> {
            if (builder.getMessageSender() == null) {
                if (builder.getOkHttpClient() == null)
                    builder.messageSender( new OkHttpMessageSender<>(builder.getMessageSerializer()));
                else
                    builder.messageSender( new OkHttpMessageSender<>(builder.getMessageSerializer(), builder.getOkHttpClient()));
            }
        });

        pipelines.add(builder -> {
            if (builder.getNettyChannelInboundHandlerFactory() == null) {
                builder.nettyChannelInboundHandlerFactory(new NettyChannelInboundHandlerFactory.DefaultNettyChannelInboundHandlerFactory());
            }
        });

        pipelines.add(builder -> {
            if (builder.getNettyKademliaMessageHandlerFactoryProvider() == null) {
                builder.nettyKademliaMessageHandlerFactoryProvider(new NettyKademliaMessageHandlerFactoryProvider.DefaultNettyKademliaMessageHandlerFactoryProvider());
            }
        });

        pipelines.add(builder -> {
            if (builder.getNettyChannelInitializerFactory() == null) {
                builder.nettyChannelInitializerFactory(new NettyChannelInitializerFactory.DefaultNettyChannelInitializerFactory(builder.getSslContext()));
            }
        });

        pipelines.add(builder -> {
            if (builder.getKademliaNodeServer() == null){
                builder.kademliaNodeServer(
                        new KademliaNodeServer<>(
                                builder.getConnectionInfo().getHost(),
                                builder.getConnectionInfo().getPort(),
                                builder.getNettyChannelInitializerFactory(),
                                builder.getNettyChannelInboundHandlerFactory(),
                                builder.getNettyKademliaMessageHandlerFactoryProvider().getNettyKademliaMessageHandlerFactory(builder)
                        )
                );
            }
        });

        return pipelines;
    }

}
