package com.tyella.netty;

import com.tyella.netty.server.NettyServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NettyApplicationTests {

    @Test
    void contextLoads() throws Throwable {
        NettyServer server = new NettyServer("127.0.0.1", 6666);
        server.doOpen();
    }

}
