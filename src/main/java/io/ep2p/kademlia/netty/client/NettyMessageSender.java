package io.ep2p.kademlia.netty.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.ep2p.kademlia.connection.MessageSender;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.node.KademliaNodeAPI;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.protocol.message.KademliaMessage;
import okhttp3.*;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyMessageSender implements MessageSender<BigInteger, NettyConnectionInfo> {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final Gson gson;
    private final OkHttpClient client;
    private final ExecutorService executorService;

    public NettyMessageSender(Gson gson, ExecutorService executorService) {
        this.gson = gson;
        this.executorService = executorService;
        this.client = new OkHttpClient();
    }

    public NettyMessageSender(Gson gson) {
        this(gson, Executors.newSingleThreadExecutor());
    }

    public NettyMessageSender(ExecutorService executorService){
        this(new GsonFactory.DefaultGsonFactory().gson(), executorService);
    }

    @Override
    public <I extends Serializable, O extends Serializable> KademliaMessage<BigInteger, NettyConnectionInfo, I> sendMessage(KademliaNodeAPI<BigInteger, NettyConnectionInfo> caller, Node<BigInteger, NettyConnectionInfo> receiver, KademliaMessage<BigInteger, NettyConnectionInfo, O> message) {

        RequestBody body = RequestBody.create(gson.toJson(message), JSON);
        Request request = new Request.Builder()
                .url(String.format("http://%s:%d/", receiver.getConnectionInfo().getHost(), receiver.getConnectionInfo().getPort()))
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return gson.fromJson(response.body().string(), new TypeToken<KademliaMessage<BigInteger, NettyConnectionInfo, Serializable>>(){}.getType());
        } catch (IOException e) {
            //todo
            throw new RuntimeException(e);
        }
    }

    @Override
    public <O extends Serializable> void sendAsyncMessage(KademliaNodeAPI<BigInteger, NettyConnectionInfo> caller, Node<BigInteger, NettyConnectionInfo> receiver, KademliaMessage<BigInteger, NettyConnectionInfo, O> message) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                sendMessage(caller, receiver, message);
            }
        });
    }

}
