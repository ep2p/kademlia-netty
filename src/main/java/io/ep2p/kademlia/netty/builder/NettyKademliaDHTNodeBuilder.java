package io.ep2p.kademlia.netty.builder;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.connection.MessageSender;
import io.ep2p.kademlia.netty.NettyKademliaDHTNode;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.KademliaMessageHandlerFactory;
import io.ep2p.kademlia.netty.factory.NettyServerInitializerFactory;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.node.builder.DHTKademliaNodeBuilder;
import io.ep2p.kademlia.repository.KademliaRepository;
import io.ep2p.kademlia.serialization.gson.GsonFactory;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.RoutingTable;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


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
    private KademliaMessageHandlerFactory<K, V> kademliaMessageHandlerFactory;
    private NettyServerInitializerFactory<K, V> nettyServerInitializerFactory;
    private final Class<K> keyClass;
    private final Class<V> valueClass;

    protected List<String> required = new ArrayList<>();

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

    public NettyKademliaDHTNodeBuilder<K, V> kademliaMessageHandlerFactory(KademliaMessageHandlerFactory<K, V> kademliaMessageHandlerFactory){
        this.kademliaMessageHandlerFactory = kademliaMessageHandlerFactory;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> nettyServerInitializerFactory(NettyServerInitializerFactory<K, V> nettyServerInitializerFactory){
        this.nettyServerInitializerFactory = nettyServerInitializerFactory;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> gsonFactory(GsonFactory gsonFactory){
        this.gsonFactory = gsonFactory;
        return this;
    }

    public NettyKademliaDHTNode<K, V> build(){
        fillDefaults();

        DHTKademliaNodeBuilder<BigInteger, NettyConnectionInfo, K, V> builder = new DHTKademliaNodeBuilder<>(
                this.id,
                this.connectionInfo,
                this.routingTable,
                this.messageSender,
                this.keyHashGenerator,
                this.repository
        );
        builder.setNodeSettings(this.nodeSettings);

        return new NettyKademliaDHTNode<>(builder.build(), this.kademliaNodeServer);
    }

    protected void fillDefaults() {
        NettyKademliaDHTNodeDefaults.run(this);
    }


}
