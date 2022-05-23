package io.ep2p.kademlia.netty.server;

import io.ep2p.kademlia.netty.common.NettyConnectionInfo;
import io.ep2p.kademlia.netty.configuration.KademliaMessageHandlerFactory;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.Serializable;
import java.math.BigInteger;

public class DefaultKademliaNodeServerInitializer<K extends Serializable, V extends Serializable> extends ChannelInitializer<SocketChannel> implements KademliaNodeServerInitializerAPI<K, V> {
    protected DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI;
    private final KademliaMessageHandlerFactory kademliaMessageHandlerFactory;
    private int frameSize = 8192;


    public DefaultKademliaNodeServerInitializer(KademliaMessageHandlerFactory kademliaMessageHandlerFactory) {
        this.kademliaMessageHandlerFactory = kademliaMessageHandlerFactory;
    }

    public DefaultKademliaNodeServerInitializer() {
        this.kademliaMessageHandlerFactory = new KademliaMessageHandlerFactory();
    }

    @Override
    public void registerKademliaNode(DHTKademliaNodeAPI<BigInteger, NettyConnectionInfo, K, V> dhtKademliaNodeAPI) {
        this.dhtKademliaNodeAPI = dhtKademliaNodeAPI;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        this.pipelineInitializer(pipeline);
    }

    @Override
    public void pipelineInitializer(ChannelPipeline pipeline) {
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(this.frameSize, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("handler", this.kademliaMessageHandlerFactory.getKademliaMessageHandler(this.dhtKademliaNodeAPI));
    }
}
