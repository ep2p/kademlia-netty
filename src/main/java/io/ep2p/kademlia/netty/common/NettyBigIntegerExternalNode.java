package io.ep2p.kademlia.netty.common;

import io.ep2p.kademlia.node.Node;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

public class NettyBigIntegerExternalNode implements Node<BigInteger, NettyConnectionInfo> {
    @Getter
    @Setter
    private NettyConnectionInfo connectionInfo;
    @Getter
    @Setter
    private BigInteger id;
    @Getter
    @Setter
    private Date lastSeen;

}
