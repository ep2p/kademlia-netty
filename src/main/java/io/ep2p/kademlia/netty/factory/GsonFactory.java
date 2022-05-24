package io.ep2p.kademlia.netty.factory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ep2p.kademlia.model.FindNodeAnswer;
import io.ep2p.kademlia.netty.common.NettyBigIntegerExternalNode;
import io.ep2p.kademlia.netty.serialization.*;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.node.external.ExternalNode;
import io.ep2p.kademlia.protocol.message.*;

public interface GsonFactory {
    Gson gson();

    class DefaultGsonFactory implements GsonFactory {

        @Override
        public Gson gson() {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder
                    .enableComplexMapKeySerialization()
                    .serializeNulls();
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            gsonBuilder.registerTypeAdapter(KademliaMessage.class, new KademliaMessageDeserializer<String, String>());
            gsonBuilder.registerTypeAdapter(DHTLookupKademliaMessage.DHTLookup.class, new DHTLookUpDeserializer<String>());
            gsonBuilder.registerTypeAdapter(DHTLookupResultKademliaMessage.DHTLookupResult.class, new DHTLookUpResultDeserializer<String, String>());
            gsonBuilder.registerTypeAdapter(DHTStoreKademliaMessage.DHTData.class, new DHTStoreDeserializer<String, String>());
            gsonBuilder.registerTypeAdapter(DHTStoreResultKademliaMessage.DHTStoreResult.class, new DHTStoreResultDeserializer<String>());
            gsonBuilder.registerTypeAdapter(ExternalNode.class, new ExternalNodeDeserializer());
            gsonBuilder.registerTypeAdapter(FindNodeAnswer.class, new FindNodeAnswerDeserializer());
            gsonBuilder.registerTypeAdapter(NettyBigIntegerExternalNode.class, new NodeDeserializer());
            gsonBuilder.registerTypeAdapter(Node.class, new NodeSerializer());
            return gsonBuilder.create();
        }
    }

}
