package com.hxz.example.client;

import com.hxz.example.imhandler.IMServerHandler;
import com.hxz.example.util.protocol.MessageDecoder;
import com.hxz.example.util.protocol.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @Author: hxz
 * @Description:
 * @Date: 2019/11/4 10:33
 */

@Slf4j
@Component
@ConfigurationProperties
public class IMClient {

    @Value("${im.server.port}")
    private int nettyPort;

    @Value("${im.server.ip}")
    private String nettyIp;

    public void sendMsg(){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // Configure SSL.
            final SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    //11 秒没有向客户端发送消息就发生心跳
                                    .addLast(sslCtx.newHandler(socketChannel.alloc(),nettyIp,nettyPort))
                                    .addLast(new IdleStateHandler(11, 0, 0))
                                    // google Protobuf 编解码
                                    //.addLast(new LengthFieldBasedFrameDecoder(1024*1024, 0, 4, 0, 0))
                                    .addLast(new MessageDecoder())
                                    .addLast(new MessageEncoder())
                                    .addLast(new IMClientHandler());
                        }
                    });

            // Start the connection attempt.
            log.info(nettyIp+":"+nettyPort);
            Channel ch = b.connect(nettyIp, nettyPort).sync().channel();

            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                // Sends the received line to the server.
                lastWriteFuture = ch.writeAndFlush(line + "\r\n");
                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    ch.closeFuture().sync();
                    break;
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        }catch (Exception e) {
            log.error("IMClient error!",e);
        }
        finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }

    }


}
