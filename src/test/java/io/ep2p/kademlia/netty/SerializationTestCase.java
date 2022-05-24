package io.ep2p.kademlia.netty;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.netty.common.NettyBigIntegerExternalNode;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.node.external.BigIntegerExternalNode;
import io.ep2p.kademlia.node.external.ExternalNode;
import io.ep2p.kademlia.protocol.message.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Date;


public class SerializationTestCase {
    private static Gson gson = null;
    private static Node<BigInteger, NettyConnectionInfo> node = null;

    @BeforeAll
    public static void initGson(){
        gson = new GsonFactory.DefaultGsonFactory().gson();
        node = NettyBigIntegerExternalNode.builder()
                .id(BigInteger.valueOf(1))
                .connectionInfo(new NettyConnectionInfo("localhost", 8000))
                .lastSeen(new Date())
                .build();
    }

    @Test
    public void testDHTLookupSerialization(){
        DHTLookupKademliaMessage<BigInteger, NettyConnectionInfo, String> kademliaMessage = new DHTLookupKademliaMessage<>();
        kademliaMessage.setData(new DHTLookupKademliaMessage.DHTLookup<>(node, "key", 1));
        kademliaMessage.setNode(node);


        String json = gson.toJson(kademliaMessage);
        System.out.println(json);

        Type type = new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, DHTLookupKademliaMessage.DHTLookup<BigInteger, NettyConnectionInfo, String>>>() {}.getType();
        KademliaMessage<BigInteger, NettyConnectionInfo, DHTLookupKademliaMessage.DHTLookup<BigInteger, NettyConnectionInfo, String>> kademliaMessage1 = gson.fromJson(json, type);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTLookupKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode(), kademliaMessage.getNode());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
        Assertions.assertEquals(kademliaMessage1.getData().getCurrentTry(), kademliaMessage.getData().getCurrentTry());
        Assertions.assertEquals(kademliaMessage1.getData().getKey(), kademliaMessage.getData().getKey());
        Assertions.assertEquals(kademliaMessage1.getData().getRequester(), kademliaMessage.getData().getRequester());
    }

    @Test
    public void testDHTLookUpSerialization(){
        DHTLookupResultKademliaMessage<BigInteger, NettyConnectionInfo, String, String> kademliaMessage = new DHTLookupResultKademliaMessage<>();
        kademliaMessage.setData(new DHTLookupResultKademliaMessage.DHTLookupResult<>(LookupAnswer.Result.FOUND, "key", "value"));
        kademliaMessage.setNode(node);


        String json = gson.toJson(kademliaMessage);
        System.out.println(json);

        Type type = new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>>(){}.getType();
        KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage1 = gson.fromJson(json, type);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTLookupResultKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode(), kademliaMessage.getNode());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    public void testDHTStoreSerialization(){
        DHTStoreKademliaMessage<BigInteger, NettyConnectionInfo, String, String> kademliaMessage = new DHTStoreKademliaMessage<>();

        kademliaMessage.setData(new DHTStoreKademliaMessage.DHTData<>(node, "key", "value"));
        kademliaMessage.setNode(node);

        String json = gson.toJson(kademliaMessage);
        System.out.println(json);

        Type type = new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>>(){}.getType();
        KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage1 = gson.fromJson(json, type);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTStoreKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode(), kademliaMessage.getNode());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    public void testDHTStoreResultSerialization(){
        DHTStoreResultKademliaMessage<BigInteger, NettyConnectionInfo, String> kademliaMessage = new DHTStoreResultKademliaMessage<>();

        kademliaMessage.setData(new DHTStoreResultKademliaMessage.DHTStoreResult<>("key", StoreAnswer.Result.STORED));
        kademliaMessage.setNode(node);

        String json = gson.toJson(kademliaMessage);
        System.out.println(json);

        Type type = new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>>(){}.getType();
        KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage1 = gson.fromJson(json, type);
        Assertions.assertTrue(kademliaMessage1 instanceof DHTStoreResultKademliaMessage);
        Assertions.assertEquals(kademliaMessage1.getType(), kademliaMessage.getType());
        Assertions.assertEquals(kademliaMessage1.getNode(), kademliaMessage.getNode());
        Assertions.assertEquals(kademliaMessage1.getData(), kademliaMessage.getData());
    }

    @Test
    public void testExternalNodeSerialization(){
        ExternalNode<BigInteger, NettyConnectionInfo> externalNode = new BigIntegerExternalNode<>(node, BigInteger.valueOf(1));

        String json = gson.toJson(externalNode);
        System.out.println(json);

        Type type = new TypeToken<ExternalNode<BigInteger, NettyConnectionInfo>>(){}.getType();
        ExternalNode<BigInteger, NettyConnectionInfo> externalNode1 = gson.fromJson(json, type);
        Assertions.assertNotNull(externalNode1);
        Assertions.assertEquals(externalNode.getId(), externalNode1.getId());
        Assertions.assertEquals(externalNode.getDistance(), externalNode1.getDistance());
    }
}
