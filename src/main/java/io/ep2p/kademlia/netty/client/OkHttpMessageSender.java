package io.ep2p.kademlia.netty.client;

import io.ep2p.kademlia.connection.MessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.node.KademliaNodeAPI;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.protocol.MessageType;
import io.ep2p.kademlia.protocol.message.KademliaMessage;
import io.ep2p.kademlia.serialization.api.MessageSerializer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
public class OkHttpMessageSender<K extends Serializable, V extends Serializable> implements MessageSender<BigInteger, NettyConnectionInfo> {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final MessageSerializer<BigInteger, NettyConnectionInfo> messageSerializer;
    private final OkHttpClient client;
    private final ExecutorService executorService;

    public OkHttpMessageSender(MessageSerializer<BigInteger, NettyConnectionInfo> messageSerializer, ExecutorService executorService) {
        this(messageSerializer, executorService, new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(64, 1, TimeUnit.MINUTES))
                .build());
    }

    public OkHttpMessageSender(MessageSerializer<BigInteger, NettyConnectionInfo> messageSerializer, ExecutorService executorService, OkHttpClient client) {
        this.messageSerializer = messageSerializer;
        this.executorService = executorService;
        this.client = client;
    }

    public OkHttpMessageSender(MessageSerializer<BigInteger, NettyConnectionInfo> messageSerializer, OkHttpClient client) {
        this(messageSerializer, Executors.newSingleThreadExecutor(), client);
    }

    public OkHttpMessageSender(MessageSerializer<BigInteger, NettyConnectionInfo> messageSerializer) {
        this(messageSerializer, Executors.newSingleThreadExecutor());
    }

    @Override
    public <U extends Serializable, O extends Serializable> KademliaMessage<BigInteger, NettyConnectionInfo, O> sendMessage(KademliaNodeAPI<BigInteger, NettyConnectionInfo> caller, Node<BigInteger, NettyConnectionInfo> receiver, KademliaMessage<BigInteger, NettyConnectionInfo, U> message) {
        message.setNode(caller);
        String messageStr = messageSerializer.serialize(message);
        RequestBody body = RequestBody.create(messageStr, JSON);
        Request request = new Request.Builder()
                .url(String.format("http://%s:%d/", receiver.getConnectionInfo().getHost(), receiver.getConnectionInfo().getPort()))
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseStr = Objects.requireNonNull(response.body()).string();
            return messageSerializer.deserialize(responseStr);
        } catch (IOException e) {
            log.error("Failed to send message to " + caller.getId(), e);
            return new KademliaMessage<BigInteger, NettyConnectionInfo, O>() {
                @Override
                public O getData() {
                    return null;
                }

                @Override
                public String getType() {
                    return MessageType.EMPTY;
                }

                @Override
                public Node<BigInteger, NettyConnectionInfo> getNode() {
                    return receiver;
                }

                @Override
                public boolean isAlive() {
                    return false;
                }
            };
        }
    }

    @Override
    public <O extends Serializable> void sendAsyncMessage(KademliaNodeAPI<BigInteger, NettyConnectionInfo> caller, Node<BigInteger, NettyConnectionInfo> receiver, KademliaMessage<BigInteger, NettyConnectionInfo, O> message) {
        executorService.submit(() -> sendMessage(caller, receiver, message));
    }

    public void stop(){
        this.executorService.shutdownNow();
    }

}
