package io.ep2p.kademlia.netty.serialization;

import com.google.gson.InstanceCreator;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.common.NettyExternalNode;
import io.ep2p.kademlia.node.Node;

import java.lang.reflect.Type;
import java.math.BigInteger;


public class NodeInstanceCreator implements InstanceCreator<Node<BigInteger, NettyConnectionInfo>> {
    @Override
    public Node<BigInteger, NettyConnectionInfo> createInstance(Type type) {
        return new NettyExternalNode();
    }
}
