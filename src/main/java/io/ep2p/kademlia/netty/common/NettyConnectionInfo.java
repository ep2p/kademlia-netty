package io.ep2p.kademlia.netty.common;

import io.ep2p.kademlia.connection.ConnectionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NettyConnectionInfo implements ConnectionInfo {
    private String host;
    private int port;
}
