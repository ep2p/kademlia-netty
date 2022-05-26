package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.connection.MessageSender;
import io.ep2p.kademlia.netty.client.NettyMessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.node.DHTKademliaNode;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.repository.KademliaRepository;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.DefaultRoutingTableFactory;
import io.ep2p.kademlia.table.RoutingTable;
import io.ep2p.kademlia.table.RoutingTableFactory;

import java.io.Serializable;

public class NettyKademliaDHTNodeBuilder<ID extends Number, K extends Serializable, V extends Serializable> {
    private ID id;
    private NettyConnectionInfo connectionInfo;
    private RoutingTable<ID, NettyConnectionInfo, Bucket<ID, NettyConnectionInfo>> routingTable;
    private MessageSender<ID, NettyConnectionInfo> messageSender;
    private NodeSettings nodeSettings;
    private KademliaRepository<K, V> repository;
    private KeyHashGenerator<ID, K> keyHashGenerator;
    private KademliaNodeServer<ID, K, V> kademliaNodeServer;


    public NettyKademliaDHTNodeBuilder() {
    }

    public NettyKademliaDHTNodeBuilder<ID, K, V> id(ID id){
        this.id = id;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<ID, K, V> connectionInfo(NettyConnectionInfo connectionInfo){
        this.connectionInfo = connectionInfo;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<ID, K, V> routingTable(RoutingTable<ID, NettyConnectionInfo, Bucket<ID, NettyConnectionInfo>> routingTable){
        this.routingTable = routingTable;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<ID, K, V> messageSender(MessageSender<ID, NettyConnectionInfo> messageSender){
        this.messageSender = messageSender;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<ID, K, V> nodeSettings(NodeSettings nodeSettings){
        this.nodeSettings = nodeSettings;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<ID, K, V> repository(KademliaRepository<K, V> repository){
        this.repository = repository;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<ID, K, V> keyHashGenerator(KeyHashGenerator<ID, K> keyHashGenerator){
        this.keyHashGenerator = keyHashGenerator;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<ID, K, V> kademliaNodeServer(KademliaNodeServer<ID, K, V> kademliaNodeServer){
        this.kademliaNodeServer = kademliaNodeServer;
        return this;
    }

    public NettyKadmliaDHTNode<ID, K, V> build(){
        if (!requiredFulfilled()){
            throw new IllegalStateException("Can not build until required parameters are set");
        }

        fillDefaults();

        DHTKademliaNodeAPI<ID, NettyConnectionInfo, K, V> kademliaNode = new DHTKademliaNode<>(
                this.id,
                this.connectionInfo,
                this.routingTable,
                this.messageSender,
                this.nodeSettings, this.repository, this.keyHashGenerator
        );

        return new NettyKadmliaDHTNode<>(kademliaNode, this.kademliaNodeServer);
    }

    protected void fillDefaults() {
        if (this.routingTable == null){
            this.setRoutingTableDefault();
        }

        if (this.messageSender == null){
            this.setMessageSenderDefault();
        }

        if (this.kademliaNodeServer == null){
            this.setKademliaNodeServer();
        }

        if (this.nodeSettings == null){
            this.setNodeSettingsDefault();
        }
    }

    private void setNodeSettingsDefault() {
        this.nodeSettings = NodeSettings.Default.build();
    }

    protected void setKademliaNodeServer() {
        this.kademliaNodeServer = new KademliaNodeServer<>(this.connectionInfo.getHost(), this.connectionInfo.getPort());
    }

    protected void setRoutingTableDefault(){
        RoutingTableFactory<ID, NettyConnectionInfo, Bucket<ID, NettyConnectionInfo>> routingTableFactory = new DefaultRoutingTableFactory<>(nodeSettings);
        this.routingTable = routingTableFactory.getRoutingTable(this.id);
    }

    protected void setMessageSenderDefault(){
        this.messageSender = new NettyMessageSender<>();
    }


    private boolean requiredFulfilled() {
        return this.id != null && this.connectionInfo != null && this.repository != null && this.keyHashGenerator != null;
    }

}
