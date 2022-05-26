package io.ep2p.kademlia.netty.server;

import com.google.gson.Gson;
import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.factory.GsonFactory;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.protocol.message.EmptyKademliaMessage;
import io.ep2p.kademlia.protocol.message.KademliaMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public abstract class AbstractKademliaMessageHandler<K extends Serializable, V extends Serializable> extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI;
    protected final Gson GSON;

    public AbstractKademliaMessageHandler(Gson gson, DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        this.GSON = gson;
        this.dhtKademliaNodeAPI = dhtKademliaNodeAPI;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        KademliaMessage<BigInteger, NettyConnectionInfo, ? extends Serializable> responseMessage = null;

        try {
            String m = this.parseJsonRequest(request);
            KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> kademliaMessage = this.toKademliaMessage(m);
            responseMessage = this.dhtKademliaNodeAPI.onMessage(kademliaMessage);
            responseMessage.setNode(this.dhtKademliaNodeAPI);
        } catch (Exception e){
            //todo
        }

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                request.protocolVersion(),
                OK,
                Unpooled.wrappedBuffer(this.GSON.toJson(responseMessage).getBytes(StandardCharsets.UTF_8))
        );

        httpResponse.headers()
                .set(CONTENT_TYPE, APPLICATION_JSON)
                .setInt(CONTENT_LENGTH, httpResponse.content().readableBytes());

        ChannelFuture f = ctx.writeAndFlush(httpResponse);
        f.addListener(ChannelFutureListener.CLOSE);
    }

    protected String parseJsonRequest(FullHttpRequest request){
        ByteBuf jsonBuf = request.content();
        return jsonBuf.toString(CharsetUtil.UTF_8);
    }

    protected abstract KademliaMessage<BigInteger, NettyConnectionInfo, Serializable> toKademliaMessage(String message);
}
