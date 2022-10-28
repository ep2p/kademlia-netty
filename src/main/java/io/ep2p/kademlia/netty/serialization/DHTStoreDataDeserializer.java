package io.ep2p.kademlia.netty.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.protocol.message.DHTStoreKademliaMessage;

import java.io.Serializable;
import java.lang.reflect.Type;


public class DHTStoreDataDeserializer<K extends Serializable, V extends Serializable> implements JsonDeserializer<DHTStoreKademliaMessage.DHTData<Long, NettyConnectionInfo, K, V>> {

    @Override
    public DHTStoreKademliaMessage.DHTData<Long, NettyConnectionInfo, K, V> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        DHTStoreKademliaMessage.DHTData<Long, NettyConnectionInfo, K, V> dhtData = new DHTStoreKademliaMessage.DHTData<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        dhtData.setKey(jsonDeserializationContext.deserialize(jsonObject.get("key"), new TypeToken<K>() {}.getType()));
        dhtData.setValue(jsonDeserializationContext.deserialize(jsonObject.get("value"), new TypeToken<V>() {}.getType()));
        dhtData.setRequester(jsonDeserializationContext.deserialize(jsonObject.getAsJsonObject("requester"), Node.class));
        return dhtData;
    }
}
