package io.ep2p.kademlia.netty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.model.FindNodeAnswer;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.common.NettyExternalNode;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.netty.serialization.GsonMessageSerializer;
import io.ep2p.kademlia.netty.serialization.MessageSerializer;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.node.external.ExternalNode;
import io.ep2p.kademlia.node.external.BigIntegerExternalNode;
import io.ep2p.kademlia.protocol.message.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;



public class SerializationTest {
    private static MessageSerializer messageSerializer;
    private static Node<BigInteger, NettyConnectionInfo> node = null;
    private static GsonBuilder gsonBuilder;

    @BeforeAll
    public static void initGson(){
        gsonBuilder = new GsonFactory.DefaultGsonFactory<String, String>().gsonBuilder();
        messageSerializer = new GsonMessageSerializer<>(gsonBuilder);
        node = NettyExternalNode.builder()
                .id(BigInteger.valueOf(1L))
                .connectionInfo(new NettyConnectionInfo("localhost", 8000))
                .build();
    }

    @Test
    void testDHTLookupSerialization(){
        DHTLookupKademliaMessage<BigInteger, NettyConnectionInfo, String> kademliaMessage = new DHTLookupKademliaMessage<>();
        kademliaMessage.setData(new DHTLookupKademliaMessage.DHTLookup<>(node, "key", 1));
        kademliaMessage.setNode(node);


        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        Type type = new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, DHTLookupKademliaMessage.DHTLookup<BigInteger, NettyConnectionInfo, String>>>() {}.getType();
        KademliaMessage<BigInteger, NettyConnectionInfo, DHTLookupKademliaMessage.DHTLookup<BigInteger, NettyConnectionInfo, String>> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTLookupKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
//        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
        Assertions.assertEquals(kademliaMessage1.getData().getCurrentTry(), kademliaMessage.getData().getCurrentTry());
        Assertions.assertEquals(kademliaMessage1.getData().getKey(), kademliaMessage.getData().getKey());
        Assertions.assertEquals(kademliaMessage1.getData().getRequester().getId(), kademliaMessage.getData().getRequester().getId());
    }

    @Test
    void testDHTLookUpSerialization(){
        DHTLookupResultKademliaMessage<BigInteger, NettyConnectionInfo, String, String> kademliaMessage = new DHTLookupResultKademliaMessage<>();
        kademliaMessage.setData(new DHTLookupResultKademliaMessage.DHTLookupResult<>(LookupAnswer.Result.FOUND, "key", "value"));
        kademliaMessage.setNode(node);


        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        Type type = new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>>(){}.getType();
        KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTLookupResultKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    void testDHTStoreSerialization() throws NoSuchFieldException, IllegalAccessException {
        DHTStoreKademliaMessage<BigInteger, NettyConnectionInfo, String, String> kademliaMessage = new DHTStoreKademliaMessage<>();

        kademliaMessage.setData(new DHTStoreKademliaMessage.DHTData<>(node, "key", "value"));
        kademliaMessage.setNode(node);

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTStoreKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Field field = kademliaMessage1.getData().getClass().getDeclaredField("key");
        field.setAccessible(true);
        Object key = field.get(kademliaMessage1.getData());
        Assertions.assertEquals(kademliaMessage.getData().getKey(), key);
    }

    @Test
    void testDHTStoreResultSerialization(){
        DHTStoreResultKademliaMessage<BigInteger, NettyConnectionInfo, String> kademliaMessage = new DHTStoreResultKademliaMessage<>();

        kademliaMessage.setData(new DHTStoreResultKademliaMessage.DHTStoreResult<>("key", StoreAnswer.Result.STORED));
        kademliaMessage.setNode(node);

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTStoreResultKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        System.out.println(kademliaMessage1.getNode().getClass());
        Assertions.assertEquals(kademliaMessage.getNode().getId(), kademliaMessage1.getNode().getId());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    void testEmptyKademliaMessageSerialization(){
        EmptyKademliaMessage<BigInteger, NettyConnectionInfo> kademliaMessage = new EmptyKademliaMessage<>();
        kademliaMessage.setNode(node);

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof EmptyKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    void testFindNodeRequestSerialization(){
        FindNodeRequestMessage<BigInteger, NettyConnectionInfo> kademliaMessage = new FindNodeRequestMessage<>();
        kademliaMessage.setNode(node);
        kademliaMessage.setData(BigInteger.valueOf(100L));

        String json = messageSerializer.serialize(kademliaMessage);
        System.out.println(json);

        KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage1 = messageSerializer.deserialize(json);
        Assertions.assertTrue(kademliaMessage1 instanceof FindNodeRequestMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode().getId(), kademliaMessage.getNode().getId());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    void testExternalNodeSerialization(){
        ExternalNode<BigInteger, NettyConnectionInfo> externalNode = new BigIntegerExternalNode<>(node, BigInteger.valueOf(1L));
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(externalNode);
        System.out.println(json);
        Type type = new TypeToken<ExternalNode<BigInteger, NettyConnectionInfo>>(){}.getType();
        ExternalNode<BigInteger, NettyConnectionInfo> externalNode1 = gson.fromJson(json, type);
        Assertions.assertNotNull(externalNode1);
        Assertions.assertEquals(externalNode.getId(), externalNode1.getId());
        Assertions.assertEquals(externalNode.getDistance(), externalNode1.getDistance());
    }

    @Test
    void testFindNodeAnswerSerialization(){
        ExternalNode<BigInteger, NettyConnectionInfo> externalNode = new BigIntegerExternalNode<>(node, BigInteger.valueOf(1L));

        FindNodeAnswer<BigInteger, NettyConnectionInfo> findNodeAnswer = new FindNodeAnswer<>(BigInteger.valueOf(1L));
        findNodeAnswer.setNodes(Collections.singletonList(new BigIntegerExternalNode<>(externalNode, BigInteger.valueOf(100L))));

        Gson gson = gsonBuilder.create();
        String json = gson.toJson(externalNode);
        System.out.println(json);

    }
}
