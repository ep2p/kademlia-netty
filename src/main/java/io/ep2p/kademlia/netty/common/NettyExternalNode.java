package io.ep2p.kademlia.netty.common;

import io.ep2p.kademlia.node.Node;
import lombok.*;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NettyExternalNode implements Node<Long, NettyConnectionInfo> {
    private NettyConnectionInfo connectionInfo;
    private Long id;

}
