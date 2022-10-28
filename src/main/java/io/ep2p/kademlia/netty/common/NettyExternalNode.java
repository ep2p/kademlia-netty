package io.ep2p.kademlia.netty.common;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.Node;
import lombok.Data;
import lombok.ToString;

import java.math.BigInteger;

@Data
@ToString
public class NettyExternalNode implements Node<BigInteger, NettyConnectionInfo> {
    private NettyConnectionInfo connectionInfo;
    private BigInteger id;

}
