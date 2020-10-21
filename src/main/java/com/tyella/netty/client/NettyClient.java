package com.tyella.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class NettyClient implements ApplicationContextAware {

    private Bootstrap bootstrap;

    private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

    private volatile Channel channel;

    private ApplicationContext applicationContext;

    private static final long NEXT_RETRY_DELAY = 5;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

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
                        ch.pipeline()
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(applicationContext.getBean(NettyClientHandler.class));
                    }
                });
        doConnect();
    }


    public void doConnect() {
        bootstrap.connect("127.0.0.1", 8883).addListener((ChannelFuture f) -> {
            if (!f.isSuccess()) {
                f.channel().eventLoop().schedule(() -> doConnect(), NEXT_RETRY_DELAY, TimeUnit.SECONDS);
            }
        });
    }

    public void write(ByteBuf byteBuf) {
        if (channel != null) {
            channel.writeAndFlush(byteBuf);
        }
    }

    public static void main(String[] args) {
        new NettyClient().doOpen();
    }
}
