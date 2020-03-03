package com.hxz.example.server;

import com.hxz.example.imhandler.IMServerHandler;
import com.hxz.example.util.protocol.MessageDecoder;
import com.hxz.example.util.protocol.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author: hxz
 * @Description: IMServer
 * @Date: 2019/11/1 14:54
 */
@Slf4j
@Component
public class IMServer {

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Value("${im.server.ip}")
    private String nettyIp;

    @Value("${im.server.port}")
    private int nettyPort;

    @PostConstruct
    public void start(){
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    //11 秒没有向客户端发送消息就发生心跳
                                    //.addLast(new IdleStateHandler(11, 0, 0))
                                    // google Protobuf 编解码
                                    //.addLast(new LengthFieldBasedFrameDecoder(1024*1024, 0, 4, 0, 0))
                                    //.addLast(new MessageDecoder())
                                    //.addLast(new MessageEncoder())
//                                    .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
//                                    .addLast(new StringDecoder())
//                                    .addLast(new StringEncoder())
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(65536))
                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new IMServerHandler(nettyIp,nettyPort));
                                    //.addLast(new ProtobufVarint32FrameDecoder())
                                    //.addLast(new ProtobufDecoder(CIMRequestProto.CIMReqProtocol.getDefaultInstance()))
                                    //.addLast(new ProtobufVarint32LengthFieldPrepender())
                                    //.addLast(new ProtobufEncoder());
                                    //.addLast();
                        }
                    });
            ChannelFuture future = bootstrap.bind(nettyIp,nettyPort).sync();
            if(future.isSuccess()){
                log.info("IMServer started on port {}", nettyPort);
                //future.channel().closeFuture().sync();
            }
        }catch (Exception e){
            log.error("IMServer started error",e);
        }
    }

    @PreDestroy
    public void destroy() {
        if(bossGroup != null){
            bossGroup.shutdownGracefully().syncUninterruptibly();
        }
        if(workerGroup != null){
            workerGroup.shutdownGracefully().syncUninterruptibly();
        }
        log.info("IMServer destroy");
    }

}
