package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.node.DHTKademliaNodeAPIDecorator;
import io.ep2p.kademlia.node.Node;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Future;

public class NettyKadmliaDHTNode<ID extends Number, K extends Serializable, V extends Serializable>
        extends DHTKademliaNodeAPIDecorator<ID, NettyConnectionInfo, K, V> {

    @Getter
    private final KademliaNodeServer<ID, K, V> kademliaNodeServer;

    public NettyKadmliaDHTNode(DHTKademliaNodeAPI<ID, NettyConnectionInfo, K, V> kademliaNode, KademliaNodeServer<ID, K, V> kademliaNodeServer) {
        super(kademliaNode);
        this.kademliaNodeServer = kademliaNodeServer;
    }

    @Override
    public void start() {
        super.start();
        kademliaNodeServer.run(this);
    }

    @Override
    public Future<Boolean> start(Node<ID, NettyConnectionInfo> bootstrapNode) {
        kademliaNodeServer.run(this);
        return super.start(bootstrapNode);
    }

    @Override
    public void stop(){
        super.stop();
        kademliaNodeServer.stop();
    }

    @Override
    public void stopNow(){
        super.stopNow();
        kademliaNodeServer.stop();
    }

    @Override
    public void setLastSeen(Date date) {}

    @Override
    public Date getLastSeen() {
        return new Date();
    }
}
