package io.ep2p.kademlia.netty.serialization;

import com.google.gson.InstanceCreator;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.common.NettyExternalNode;
import io.ep2p.kademlia.node.Node;

import java.lang.reflect.Type;


public class NodeInstanceCreator implements InstanceCreator<Node<Long, NettyConnectionInfo>> {
    @Override
    public Node<Long, NettyConnectionInfo> createInstance(Type type) {
        return new NettyExternalNode();
    }
}
