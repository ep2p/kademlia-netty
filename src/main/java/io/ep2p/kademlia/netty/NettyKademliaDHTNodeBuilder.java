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
import java.math.BigInteger;

public class NettyKademliaDHTNodeBuilder<K extends Serializable, V extends Serializable> {
    private BigInteger id;
    private NettyConnectionInfo connectionInfo;
    private RoutingTable<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTable;
    private MessageSender<BigInteger, NettyConnectionInfo> messageSender;
    private NodeSettings nodeSettings;
    private KademliaRepository<K, V> repository;
    private KeyHashGenerator<BigInteger, K> keyHashGenerator;
    private KademliaNodeServer<K, V> kademliaNodeServer;


    public NettyKademliaDHTNodeBuilder() {
    }

    public NettyKademliaDHTNodeBuilder<K, V> id(BigInteger id){
        this.id = id;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> connectionInfo(NettyConnectionInfo connectionInfo){
        this.connectionInfo = connectionInfo;
        return this;
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

    public NettyKademliaDHTNodeBuilder<K, V> repository(KademliaRepository<K, V> repository){
        this.repository = repository;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> keyHashGenerator(KeyHashGenerator<BigInteger, K> keyHashGenerator){
        this.keyHashGenerator = keyHashGenerator;
        return this;
    }

    public NettyKademliaDHTNodeBuilder<K, V> kademliaNodeServer(KademliaNodeServer<K, V> kademliaNodeServer){
        this.kademliaNodeServer = kademliaNodeServer;
        return this;
    }

    public NettyKadmliaDHTNode<K, V> build(){
        if (!requiredFulfilled()){
            throw new IllegalStateException("Can not build until required parameters are set");
        }

        fillDefaults();

        DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> kademliaNode = new DHTKademliaNode<>(
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
        RoutingTableFactory<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTableFactory = new DefaultRoutingTableFactory<>(nodeSettings);
        this.routingTable = routingTableFactory.getRoutingTable(this.id);
    }

    protected void setMessageSenderDefault(){
        this.messageSender = new NettyMessageSender();
    }


    private boolean requiredFulfilled() {
        return this.id != null && this.connectionInfo != null && this.repository != null && this.keyHashGenerator != null;
    }

}
