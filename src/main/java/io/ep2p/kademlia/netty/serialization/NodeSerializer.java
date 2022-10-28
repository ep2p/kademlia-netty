package io.ep2p.kademlia.netty.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.Node;

import java.lang.reflect.Type;

public class NodeSerializer implements JsonSerializer<Node<Long, NettyConnectionInfo>> {
    @Override
    public JsonElement serialize(Node<Long, NettyConnectionInfo> src, Type type, JsonSerializationContext context) {
        JsonObject jsonNode = new JsonObject();
        jsonNode.addProperty("id", src.getId());
        jsonNode.add("connectionInfo", context.serialize(src.getConnectionInfo(), NettyConnectionInfo.class));
        return jsonNode;
    }
}
