package io.ep2p.kademlia.netty;

import java.io.IOException;
import java.net.ServerSocket;

public class NodeHelper {

    public static Integer findRandomPort() throws IOException {
        try (
            ServerSocket socket = new ServerSocket(0);
        ) {
            return socket.getLocalPort();
        }
    }

}
