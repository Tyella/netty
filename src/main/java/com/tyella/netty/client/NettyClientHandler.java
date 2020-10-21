package com.tyella.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.ref.Reference;
import java.nio.charset.StandardCharsets;

@Component
@Scope("protype")
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Log log = LogFactory.getLog(NettyClient.class);

    @Autowired
    private NettyClient nettyClient;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.fireChannelRead(msg);
        try {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] respByte = new byte[byteBuf.readableBytes()];
            String msgStr = new String(respByte, StandardCharsets.UTF_8);
            //todo
            System.out.println(msgStr);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        log.info("The connection of: " + channel.localAddress()
                + " -> " + channel.remoteAddress() + " is established.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        log.info("The connection of: " + channel.localAddress()
                + " -> " + channel.remoteAddress() + " is disconnected.");
        nettyClient.doConnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
