package io.ep2p.kademlia.netty.builder;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.connection.MessageSender;
import io.ep2p.kademlia.netty.NettyKademliaDHTNode;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.netty.factory.KademliaMessageHandlerFactory;
import io.ep2p.kademlia.netty.factory.NettyServerInitializerFactory;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.node.DHTKademliaNode;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.repository.KademliaRepository;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.RoutingTable;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
public class NettyKademliaDHTNodeBuilder<K extends Serializable, V extends Serializable> {
    private final Long id;
    private final NettyConnectionInfo connectionInfo;
    private RoutingTable<Long, NettyConnectionInfo, Bucket<Long, NettyConnectionInfo>> routingTable;
    private MessageSender<Long, NettyConnectionInfo> messageSender;
    private NodeSettings nodeSettings;
    private GsonFactory gsonFactory;
    private final KademliaRepository<K, V> repository;
    private final KeyHashGenerator<Long, K> keyHashGenerator;
    private KademliaNodeServer<K, V> kademliaNodeServer;
    private KademliaMessageHandlerFactory<K, V> kademliaMessageHandlerFactory;
    private NettyServerInitializerFactory<K, V> nettyServerInitializerFactory;

    protected List<String> required = new ArrayList<>();

    public NettyKademliaDHTNodeBuilder(Long id, NettyConnectionInfo connectionInfo, KademliaRepository<K, V> repository, KeyHashGenerator<Long, K> keyHashGenerator) {
        this.id = id;
        this.connectionInfo = connectionInfo;
        this.repository = repository;
        this.keyHashGenerator = keyHashGenerator;
    }

    public NettyKademliaDHTNodeBuilder<K, V> routingTable(RoutingTable<Long, NettyConnectionInfo, Bucket<Long, NettyConnectionInfo>> routingTable){
        this.routingTable = routingTable;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> messageSender(MessageSender<Long, NettyConnectionInfo> messageSender){
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

        DHTKademliaNodeAPI<Long, NettyConnectionInfo, K, V> kademliaNode = new DHTKademliaNode<>(
                this.id,
                this.connectionInfo,
                this.routingTable,
                this.messageSender,
                this.nodeSettings, this.repository, this.keyHashGenerator
        );

        return new NettyKademliaDHTNode<>(kademliaNode, this.kademliaNodeServer);
    }

    protected void fillDefaults() {
        NettyKademliaDHTNodeDefaults.run(this);
    }


}
