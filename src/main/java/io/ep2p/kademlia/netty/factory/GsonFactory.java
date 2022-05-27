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

import java.io.Serializable;

public interface GsonFactory {
    Gson gson();

    class DefaultGsonFactory<K extends Serializable, V extends Serializable> implements GsonFactory {

        @Override
        public Gson gson() {
            GsonBuilder gsonBuilder = new GsonBuilder();
            return gsonBuilder
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(KademliaMessage.class, new KademliaMessageDeserializer<K, V>())
                .registerTypeAdapter(DHTLookupKademliaMessage.DHTLookup.class, new DHTLookUpDeserializer<K>())
                .registerTypeAdapter(DHTLookupResultKademliaMessage.DHTLookupResult.class, new DHTLookUpResultDeserializer<K, V>())
                .registerTypeAdapter(DHTStoreKademliaMessage.DHTData.class, new DHTStoreDeserializer<K, V>())
                .registerTypeAdapter(DHTStoreResultKademliaMessage.DHTStoreResult.class, new DHTStoreResultDeserializer<K>())
                .registerTypeAdapter(ExternalNode.class, new ExternalNodeDeserializer())
                .registerTypeAdapter(FindNodeAnswer.class, new FindNodeAnswerDeserializer())
                .registerTypeAdapter(NettyBigIntegerExternalNode.class, new NodeDeserializer())
                .registerTypeAdapter(Node.class, new NodeSerializer())
                .create();
        }
    }

}
