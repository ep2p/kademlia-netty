package io.ep2p.kademlia.netty.builder;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.connection.MessageSender;
import io.ep2p.kademlia.netty.NettyKademliaDHTNode;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.NettyChannelInboundHandlerFactory;
import io.ep2p.kademlia.netty.factory.NettyChannelInitializerFactory;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.node.builder.DHTKademliaNodeBuilder;
import io.ep2p.kademlia.repository.KademliaRepository;
import io.ep2p.kademlia.serialization.api.MessageSerializer;
import io.ep2p.kademlia.serialization.gson.GsonFactory;
import io.ep2p.kademlia.services.DHTLookupServiceFactory;
import io.ep2p.kademlia.services.DHTStoreServiceFactory;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.RoutingTable;
import io.netty.handler.ssl.SslContext;
import lombok.Getter;
import okhttp3.OkHttpClient;

import java.io.Serializable;
import java.math.BigInteger;


@Getter
public class NettyKademliaDHTNodeBuilder<K extends Serializable, V extends Serializable> {
    private final BigInteger id;
    private final NettyConnectionInfo connectionInfo;
    private RoutingTable<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTable;
    private MessageSender<BigInteger, NettyConnectionInfo> messageSender;
    private NodeSettings nodeSettings;
    private GsonFactory gsonFactory;
    private final KademliaRepository<K, V> repository;
    private final KeyHashGenerator<BigInteger, K> keyHashGenerator;
    private KademliaNodeServer<K, V> kademliaNodeServer;
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    private OkHttpClient okHttpClient;
    private MessageSerializer<BigInteger, NettyConnectionInfo> messageSerializer;
    private NettyKademliaMessageHandlerFactoryProvider nettyKademliaMessageHandlerFactoryProvider;
    private NettyChannelInboundHandlerFactory nettyChannelInboundHandlerFactory;
    private NettyChannelInitializerFactory nettyChannelInitializerFactory;
    private DHTStoreServiceFactory<BigInteger, NettyConnectionInfo, K, V> dhtStoreServiceFactory;
    private DHTLookupServiceFactory<BigInteger, NettyConnectionInfo, K, V> dhtLookupServiceFactory;
    private SslContext sslContext;

    public NettyKademliaDHTNodeBuilder(BigInteger id, NettyConnectionInfo connectionInfo, KademliaRepository<K, V> repository, KeyHashGenerator<BigInteger, K> keyHashGenerator, Class<K> keyClass, Class<V> valueClass) {
        this.id = id;
        this.connectionInfo = connectionInfo;
        this.repository = repository;
        this.keyHashGenerator = keyHashGenerator;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public NettyKademliaDHTNodeBuilder<K, V> routingTable(RoutingTable<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTable){
        this.routingTable = routingTable;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> messageSender(MessageSender<BigInteger, NettyConnectionInfo> messageSender){
        this.messageSender = messageSender;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> nodeSettings(NodeSettings nodeSettings){
        this.nodeSettings = nodeSettings;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> kademliaNodeServer(KademliaNodeServer<K, V> kademliaNodeServer){
        this.kademliaNodeServer = kademliaNodeServer;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> gsonFactory(GsonFactory gsonFactory){
        this.gsonFactory = gsonFactory;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> messageSerializer(MessageSerializer<BigInteger, NettyConnectionInfo> messageSerializer){
        this.messageSerializer = messageSerializer;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> okHttpClient(OkHttpClient client){
        this.okHttpClient = client;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> nettyKademliaMessageHandlerFactoryProvider(NettyKademliaMessageHandlerFactoryProvider nettyKademliaMessageHandlerFactoryProvider){
        this.nettyKademliaMessageHandlerFactoryProvider = nettyKademliaMessageHandlerFactoryProvider;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> nettyChannelInboundHandlerFactory(NettyChannelInboundHandlerFactory nettyChannelInboundHandlerFactory){
        this.nettyChannelInboundHandlerFactory = nettyChannelInboundHandlerFactory;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> nettyChannelInitializerFactory(NettyChannelInitializerFactory nettyChannelInitializerFactory){
        this.nettyChannelInitializerFactory = nettyChannelInitializerFactory;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> sslContext(SslContext sslContext){
        this.sslContext = sslContext;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> dhtStoreServiceFactory(DHTStoreServiceFactory<BigInteger, NettyConnectionInfo, K, V> dhtStoreServiceFactory){
        this.dhtStoreServiceFactory = dhtStoreServiceFactory;
        return this;
    }

    private NettyKademliaDHTNodeBuilder<K, V> dhtLookupServiceFactory(DHTLookupServiceFactory<BigInteger, NettyConnectionInfo, K, V> dhtLookupServiceFactory){
        this.dhtLookupServiceFactory = dhtLookupServiceFactory;
        return this;
    }

    protected DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> buildDHTKademliaNodeAPI(){
        DHTKademliaNodeBuilder<BigInteger, NettyConnectionInfo, K, V> builder = new DHTKademliaNodeBuilder<>(
                this.id,
                this.connectionInfo,
                this.routingTable,
                this.messageSender,
                this.keyHashGenerator,
                this.repository
        );
        if (this.getDhtLookupServiceFactory() != null){
            builder.setDhtLookupServiceFactory(this.getDhtLookupServiceFactory());
        }
        if (this.getDhtStoreServiceFactory() != null){
            builder.setDhtStoreServiceFactory(this.getDhtStoreServiceFactory());
        }
        return builder.setNodeSettings(this.nodeSettings).build();
    }

    public NettyKademliaDHTNode<K, V> build(){
        fillDefaults();
        return new NettyKademliaDHTNode<>(buildDHTKademliaNodeAPI(), this.kademliaNodeServer);
    }

    protected void fillDefaults() {
        NettyKademliaDHTNodeDefaults.run(this);
    }


}
