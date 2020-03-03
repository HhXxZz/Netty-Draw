package com.hxz.example.client;

import com.hxz.example.util.protocol.MessageRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: hxz
 * @Description:
 * @Date: 2019/11/4 10:47
 */
@Slf4j
public class IMClientHandler extends SimpleChannelInboundHandler<MessageRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageRequest messageRequest) throws Exception {

        log.info("发送了："+messageRequest.getMsg());

    }
}
