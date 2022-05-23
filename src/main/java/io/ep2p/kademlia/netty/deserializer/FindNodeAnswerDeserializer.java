package io.ep2p.kademlia.netty.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.model.FindNodeAnswer;
import io.ep2p.kademlia.node.external.ExternalNode;

import java.lang.reflect.Type;
import java.math.BigInteger;

public class FindNodeAnswerDeserializer implements JsonDeserializer<FindNodeAnswer<BigInteger, NettyConnectionInfo>> {
    @Override
    public FindNodeAnswer<BigInteger, NettyConnectionInfo> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        FindNodeAnswer<BigInteger, NettyConnectionInfo> findNodeAnswer = new FindNodeAnswer<>();
        findNodeAnswer.setDestinationId(jsonElement.getAsJsonObject().get("destinationId").getAsBigInteger());
        findNodeAnswer.setNodes(jsonDeserializationContext.deserialize(
                jsonElement.getAsJsonObject().get("nodes").getAsJsonArray(),
                new TypeToken<ExternalNode<BigInteger, NettyConnectionInfo>>(){}.getType()
        ));
        return findNodeAnswer;
    }
}
