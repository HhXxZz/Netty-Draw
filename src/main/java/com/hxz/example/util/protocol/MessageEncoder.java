package com.hxz.example.util.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<MessageResponse> {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);
	
    @Override
    public void encode(ChannelHandlerContext ctx, MessageResponse in, ByteBuf out) throws Exception {
    	try{
			byte[] data = SerializationUtil.serialize(in);
			out.writeInt(data.length);
			out.writeBytes(data);
    	}catch(Exception e){
    		logger.error("encode", e);
    	}
    }
}
