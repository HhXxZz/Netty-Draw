package com.hxz.example.imhandler;

import com.alibaba.fastjson.JSONObject;
import com.hxz.example.entity.Room;
import com.hxz.example.entity.User;
import com.hxz.example.util.protocol.MessageRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpUtil.formatHostnameForHttp;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

/**
 * @Author: hxz
 * @Description:
 * @Date: 2019/11/1 15:23
 */
@Slf4j
public class IMServerHandler extends SimpleChannelInboundHandler<Object> {

    private static List<Room> roomList = new CopyOnWriteArrayList<>();

    static {
        Room room = new Room();
        room.setRoomId("10001");
        room.setGlobalGroup(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        room.setUserList(new ArrayList<>());
        roomList.add(room);

        room = new Room();
        room.setRoomId("10002");
        room.setGlobalGroup(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        room.setUserList(new ArrayList<>());
        roomList.add(room);
    }

    private WebSocketServerHandshaker handshaker;

    private String ip;
    private int port;
    public IMServerHandler(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        log.info("channelRead0,收到消息"+ctx.channel().id()+","+obj);

        if(obj instanceof FullHttpRequest){

            FullHttpRequest req = (FullHttpRequest) obj;
            log.info("httpUrl:{}",req.uri());
            //要求Upgrade为websocket，过滤掉get/Post
            if (!req.decoderResult().isSuccess()
                    || (!"websocket".equals(req.headers().get("Upgrade")))) {
                //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return;
            }

            String webSocketURL = "ws://"+ip+":"+port;
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketURL, null, false);
            handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory
                        .sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
            }

        }else if(obj instanceof WebSocketFrame){
            log.info("webSocket");
            handlerWebSocket(ctx, (WebSocketFrame) obj);
        }

    }


    private void handlerWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame){

        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            log.info("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }
        // 返回应答消息
        String requestStr = ((TextWebSocketFrame) frame).text();
        log.info("服务端收到：" + requestStr);

        JSONObject requestInfo = JSONObject.parseObject(requestStr);
        for(String actionKey : requestInfo.keySet()){
            if("login".equals(actionKey)){
                JSONObject msgInfo = requestInfo.getJSONObject("login");
                String userId = msgInfo.getString("userId");
                String roomId = msgInfo.getString("roomId");

                User user = new User();
                user.setUserId(userId);
                user.setChannelId(ctx.channel().id().asShortText());

                for(Room room : roomList){
                    if(roomId.equals(room.getRoomId())){
                        ChannelGroup globalGroup = room.getGlobalGroup();
                        List<User>userList = room.getUserList();
                        globalGroup.add(ctx.channel());
                        userList.add(user);
                        TextWebSocketFrame tws = new TextWebSocketFrame(response(userId,"im","欢迎用户:"+userId+"进入房间号:"+roomId));
                        globalGroup.writeAndFlush(tws);
                        break;
                    }
                }
            }else if("im".equals(actionKey)){
                JSONObject msgInfo = requestInfo.getJSONObject("im");
                String userId = msgInfo.getString("userId");
                String roomId = msgInfo.getString("roomId");
                String msg = msgInfo.getString("msg");
                for(Room room : roomList){
                    if(roomId.equals(room.getRoomId())){
                        ChannelGroup globalGroup = room.getGlobalGroup();
                        TextWebSocketFrame tws = new TextWebSocketFrame(response(userId,"im","用户 "+userId+" ："+msg));
                        globalGroup.writeAndFlush(tws);
                        break;
                    }
                }
            }else if("exit".equals(actionKey)){

            }else if("paint".equals(actionKey)){
                JSONObject msgInfo = requestInfo.getJSONObject("paint");
                String userId = msgInfo.getString("userId");
                String roomId = msgInfo.getString("roomId");
                JSONObject xyInfo = msgInfo.getJSONObject("data");
                for(Room room : roomList){
                    if(roomId.equals(room.getRoomId())){
                        ChannelGroup globalGroup = room.getGlobalGroup();
                        TextWebSocketFrame tws = new TextWebSocketFrame(response(userId,"paint",xyInfo));
                        globalGroup.writeAndFlush(tws);
                        break;
                    }
                }
            }
        }

    }

    private String response(String userId,String type, Object data){
        JSONObject responseInfo = new JSONObject();
        responseInfo.put("userId",userId);
        responseInfo.put("type",type);
        responseInfo.put("data",data);
        return responseInfo.toJSONString();
    }

    /**
     * 拒绝不合法的请求，并返回错误信息
     * */
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

    }

//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        super.channelRegistered(ctx);
//
//        log.info("channelRegistered");
//    }

//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        super.channelUnregistered(ctx);
//        log.info("channelUnregistered");
//    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("IMServerHandler.error",cause);

    }
}

