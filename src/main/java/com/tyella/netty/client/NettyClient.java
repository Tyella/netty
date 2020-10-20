package com.tyella.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NettyClient {

    private Bootstrap bootstrap;

    private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

    private volatile Channel channel;

    private static final long NEXT_RETRY_DELAY = 5;

    /**
     * Init bootstrap
     *
     * @throws Throwable
     */
    public void doOpen() {
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(SocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
        doConnect();
    }


    public void doConnect() {
        bootstrap.connect().addListener((ChannelFuture f) -> {
            if (!f.isSuccess()) {
                f.channel().eventLoop().schedule(() -> doConnect(), NEXT_RETRY_DELAY, TimeUnit.SECONDS);
            }
        });
    }
}
