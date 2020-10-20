package com.tyella.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NettyServer {

    private static final Log log = LogFactory.getLog(NettyServer.class);


    private ServerBootstrap serverBootstrap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private String ip;

    private int port;

    public NettyServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Init and start netty server
     *
     * @throws Throwable
     */
    public void doOpen() throws Throwable {
        serverBootstrap = new ServerBootstrap();
        //todo thread group name
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(1);

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {

                    }
                });
        serverBootstrap.bind(port).sync();
        log.info("server bind localhost:" + 6666);
    }

    protected void doClose() {
        log.info("Netty-server stopped.");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

}
