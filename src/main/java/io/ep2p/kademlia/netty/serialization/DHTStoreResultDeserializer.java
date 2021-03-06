package io.ep2p.kademlia.netty.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.protocol.message.DHTStoreResultKademliaMessage;

import java.io.Serializable;
import java.lang.reflect.Type;

public class DHTStoreResultDeserializer<K extends Serializable> implements JsonDeserializer<DHTStoreResultKademliaMessage.DHTStoreResult<K>> {

    @Override
    public DHTStoreResultKademliaMessage.DHTStoreResult<K> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        DHTStoreResultKademliaMessage.DHTStoreResult<K> dhtStoreResult = new DHTStoreResultKademliaMessage.DHTStoreResult<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        dhtStoreResult.setKey(jsonDeserializationContext.deserialize(jsonObject.get("key"), new TypeToken<K>() {}.getType()));
        dhtStoreResult.setResult(StoreAnswer.Result.valueOf(jsonObject.get("result").getAsString()));
        return dhtStoreResult;
    }
}
