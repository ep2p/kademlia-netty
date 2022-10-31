package io.ep2p.kademlia.netty.common;

import io.ep2p.kademlia.node.Node;
import lombok.*;

import java.math.BigInteger;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NettyExternalNode implements Node<BigInteger, NettyConnectionInfo> {
    private NettyConnectionInfo connectionInfo;
    private BigInteger id;

}
