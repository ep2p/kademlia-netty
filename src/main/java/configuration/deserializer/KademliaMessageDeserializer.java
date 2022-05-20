package configuration.deserializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import common.NettyBigIntegerExternalNode;
import common.NettyConnectionInfo;
import io.ep2p.kademlia.model.FindNodeAnswer;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.protocol.MessageType;
import io.ep2p.kademlia.protocol.message.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class KademliaMessageDeserializer<K extends Serializable, V extends Serializable> implements JsonDeserializer<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>> {
    private final Gson gson = new Gson();
    private final Map<String, Type> registry = new ConcurrentHashMap<>();

    // Todo: implement deserializers
    public KademliaMessageDeserializer() {
        this.registerDataType(MessageType.DHT_LOOKUP, new TypeToken<DHTLookupKademliaMessage.DHTLookup<BigInteger, NettyConnectionInfo, K>>(){}.getType());
        this.registerDataType(MessageType.DHT_LOOKUP_RESULT, new TypeToken<DHTLookupResultKademliaMessage.DHTLookupResult<K, V>>(){}.getType());
        this.registerDataType(MessageType.DHT_STORE, new TypeToken<DHTStoreKademliaMessage.DHTData<BigInteger, NettyConnectionInfo, K, V>>(){}.getType());
        this.registerDataType(MessageType.DHT_STORE_RESULT, new TypeToken<DHTStoreResultKademliaMessage.DHTStoreResult<K>>(){}.getType());
        this.registerDataType(MessageType.FIND_NODE_REQ, new TypeToken<BigInteger>(){}.getType());
        this.registerDataType(MessageType.FIND_NODE_RES, new TypeToken<FindNodeAnswer<BigInteger, NettyConnectionInfo>>(){}.getType());
        this.registerDataType(MessageType.PING, new TypeToken<String>(){}.getType());
        this.registerDataType(MessageType.PONG, new TypeToken<String>(){}.getType());
        this.registerDataType(MessageType.SHUTDOWN, new TypeToken<String>(){}.getType());
    }

    @Override
    public KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String type_ = jsonObject.getAsJsonPrimitive("type").getAsString();

        KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage = new KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>(type_) {
            @Override
            public Serializable getData() {
                if (this.getType().equals(MessageType.EMPTY))
                    return null;
                Type dataType = registry.get(this.getType());
                if (dataType != null){
                    return jsonDeserializationContext.deserialize(
                            jsonObject.getAsJsonObject("data"),
                            dataType
                            );
                }
                return null;
            }

            @Override
            public Node<BigInteger, NettyConnectionInfo> getNode() {
                return jsonDeserializationContext.deserialize(
                        jsonObject.getAsJsonObject("node"),
                        NettyBigIntegerExternalNode.class
                );
            }

            @Override
            public boolean isAlive() {
                return true;
            }
        };

        return null;
    }

    public void registerDataType(String name, Type type){
        this.registry.put(name, type);
    }
}
