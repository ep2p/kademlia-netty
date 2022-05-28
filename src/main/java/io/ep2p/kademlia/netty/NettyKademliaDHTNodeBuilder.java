package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.connection.MessageSender;
import io.ep2p.kademlia.netty.client.NettyMessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.netty.factory.KademliaMessageHandlerFactory;
import io.ep2p.kademlia.netty.factory.NettyServerInitializerFactory;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.netty.server.filter.KademliaMainHandlerFilter;
import io.ep2p.kademlia.netty.server.filter.NettyKademliaServerFilterChain;
import io.ep2p.kademlia.node.DHTKademliaNode;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.repository.KademliaRepository;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.DefaultRoutingTableFactory;
import io.ep2p.kademlia.table.RoutingTable;
import io.ep2p.kademlia.table.RoutingTableFactory;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Getter
public class NettyKademliaDHTNodeBuilder<K extends Serializable, V extends Serializable> {
    private BigInteger id;
    private NettyConnectionInfo connectionInfo;
    private RoutingTable<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTable;
    private MessageSender<BigInteger, NettyConnectionInfo> messageSender;
    private NodeSettings nodeSettings;
    private GsonFactory gsonFactory;
    private KademliaRepository<K, V> repository;
    private KeyHashGenerator<BigInteger, K> keyHashGenerator;
    private KademliaNodeServer<K, V> kademliaNodeServer;
    private KademliaMessageHandlerFactory<K, V> kademliaMessageHandlerFactory;
    private NettyServerInitializerFactory<K, V> nettyServerInitializerFactory;

    protected List<String> required = new ArrayList<>();

    public NettyKademliaDHTNodeBuilder() {
        this.required.add("id");
        this.required.add("connectionInfo");
        this.required.add("repository");
        this.required.add("keyHashGenerator");
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

        return new NettyKademliaDHTNode<>(kademliaNode, this.kademliaNodeServer);
    }

    protected void fillDefaults() {
        if (this.routingTable == null){
            this.setRoutingTableDefault();
        }

        if (this.messageSender == null){
            this.setMessageSenderDefault();
        }

        if (this.gsonFactory == null){
            this.gsonFactoryDefault();
        }

        if (this.kademliaMessageHandlerFactory == null){
            this.setKademliaMessageHandlerFactoryDefault();
        }

        if (this.nettyServerInitializerFactory == null){
            this.setNettyServerInitializerFactoryDefault();
        }

        if (this.kademliaNodeServer == null){
            this.setKademliaNodeServerDefault();
        }

        if (this.nodeSettings == null){
            this.setNodeSettingsDefault();
        }
    }

    private void setKademliaMessageHandlerFactoryDefault(){
        NettyKademliaServerFilterChain<K, V> filterChain = new NettyKademliaServerFilterChain<>();
        filterChain.addFilter(new KademliaMainHandlerFilter<>(gsonFactory.gson()));
        this.kademliaMessageHandlerFactory = new KademliaMessageHandlerFactory.DefaultKademliaMessageHandlerFactory<>(filterChain);
    }

    private void gsonFactoryDefault() {
        this.gsonFactory = new GsonFactory.DefaultGsonFactory<>();
    }

    private void setNettyServerInitializerFactoryDefault() {
        this.nettyServerInitializerFactory = new NettyServerInitializerFactory.DefaultNettyServerInitializerFactory<>(this.kademliaMessageHandlerFactory);
    }

    private void setNodeSettingsDefault() {
        this.nodeSettings = NodeSettings.Default.build();
    }

    protected void setKademliaNodeServerDefault() {
        this.kademliaNodeServer = new KademliaNodeServer<>(this.connectionInfo.getHost(), this.connectionInfo.getPort(), this.nettyServerInitializerFactory);
    }

    protected void setRoutingTableDefault(){
        RoutingTableFactory<BigInteger, NettyConnectionInfo, Bucket<BigInteger, NettyConnectionInfo>> routingTableFactory = new DefaultRoutingTableFactory<>(nodeSettings);
        this.routingTable = routingTableFactory.getRoutingTable(this.id);
    }

    protected void setMessageSenderDefault(){
        this.messageSender = new NettyMessageSender<K, V>();
    }


    @SneakyThrows
    protected boolean requiredFulfilled() {
        for (String f : this.required) {
            if (this.getClass().getDeclaredField(f).get(this) == null) {
                return false;
            }
        }
        return true;
    }

}
