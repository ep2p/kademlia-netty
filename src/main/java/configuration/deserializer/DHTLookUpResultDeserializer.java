package configuration.deserializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import common.NettyBigIntegerExternalNode;
import common.NettyConnectionInfo;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.protocol.message.DHTLookupKademliaMessage;
import io.ep2p.kademlia.protocol.message.DHTLookupResultKademliaMessage;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigInteger;

public class DHTLookUpResultDeserializer<K extends Serializable, V extends Serializable> implements JsonDeserializer<DHTLookupResultKademliaMessage.DHTLookupResult<K, V>> {

    @Override
    public DHTLookupResultKademliaMessage.DHTLookupResult<K, V> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        DHTLookupResultKademliaMessage.DHTLookupResult<K, V> dhtLookupResult = new DHTLookupResultKademliaMessage.DHTLookupResult<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        dhtLookupResult.setKey(jsonDeserializationContext.deserialize(jsonObject.getAsJsonObject("key"), new TypeToken<K>() {}.getType()));
        dhtLookupResult.setValue(jsonDeserializationContext.deserialize(jsonObject.getAsJsonObject("value"), new TypeToken<V>() {}.getType()));
        dhtLookupResult.setResult(LookupAnswer.Result.valueOf(jsonObject.get("result").getAsString()));
        return dhtLookupResult;
    }
}