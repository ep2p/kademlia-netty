package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.node.DHTKademliaNodeAPIDecorator;
import lombok.Getter;
import io.ep2p.kademlia.netty.server.KademliaNodeServer;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

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
        try {
            kademliaNodeServer.run(this);
            super.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop(){
        kademliaNodeServer.stop();
        super.stop();
    }

    @Override
    public void stopNow(){
        kademliaNodeServer.stop();
        super.stopNow();
    }

    @Override
    public void setLastSeen(Date date) {}

    @Override
    public Date getLastSeen() {
        return new Date();
    }
}
