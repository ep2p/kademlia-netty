package io.ep2p.kademlia.netty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;


@Slf4j
@ChannelHandler.Sharable
public class NettyKademliaChannelInboundHandler<K extends Serializable, V extends Serializable> extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final NettyKademliaMessageHandler nettyKademliaMessageHandler;

    public NettyKademliaChannelInboundHandler(NettyKademliaMessageHandler nettyKademliaMessageHandler) {
        this.nettyKademliaMessageHandler = nettyKademliaMessageHandler;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                request.protocolVersion(),
                OK
        );
        this.nettyKademliaMessageHandler.handle(channelHandlerContext, request, httpResponse);
        httpResponse.headers().setInt(CONTENT_LENGTH, httpResponse.content().readableBytes());
        if (keepAlive) {
            httpResponse.headers().set(CONNECTION, KEEP_ALIVE);
        } else {
            httpResponse.headers().set(CONNECTION, CLOSE);
        }
        channelHandlerContext.write(httpResponse);

        if (!keepAlive) {
            channelHandlerContext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Channel handler caught exception", cause);
        ctx.close();
    }
}
