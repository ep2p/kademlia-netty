package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.node.DHTKademliaNodeAPIDecorator;
import io.ep2p.kademlia.node.Node;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.Future;

public class NettyKadmliaDHTNode<K extends Serializable, V extends Serializable>
        extends DHTKademliaNodeAPIDecorator<BigInteger, NettyConnectionInfo, K, V> {

    @Getter
    private final KademliaNodeServer<K, V> kademliaNodeServer;

    public NettyKadmliaDHTNode(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> kademliaNode, KademliaNodeServer<K, V> kademliaNodeServer) {
        super(kademliaNode);
        this.kademliaNodeServer = kademliaNodeServer;
    }

    @Override
    public void start() {
        super.start();
        kademliaNodeServer.run(this);
    }

    @Override
    public Future<Boolean> start(Node<BigInteger, NettyConnectionInfo> bootstrapNode) {
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
