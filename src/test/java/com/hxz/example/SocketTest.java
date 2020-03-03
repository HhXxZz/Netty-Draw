package com.hxz.example;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 * @Author: hxz
 * @Description:
 * @Date: 2020/2/25 14:53
 */
public class SocketTest {

    public static void main(String[] args) {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        Request request = new Request.Builder().url("ws://127.0.0.1:8888").build();
        EchoWebSocketListener socketListener = new EchoWebSocketListener();

        // 刚进入界面，就开启心跳检测
        //mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);

        mOkHttpClient.newWebSocket(request, socketListener);

        //mOkHttpClient.dispatcher().executorService().shutdown();
    }



    static final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            System.out.println("open");
            try {
                System.out.println("onOpen:"+response.body().string());
            }catch (Exception e){
                e.printStackTrace();
            }
            //open时传入登录信息

            JSONObject loginInfo = new JSONObject();
            loginInfo.put("userId","2");
            loginInfo.put("roomId","10001");

            JSONObject actionInfo = new JSONObject();
            actionInfo.put("login",loginInfo);
            webSocket.send(actionInfo.toJSONString());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            System.out.println("onMessage:"+text);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            System.out.println("onMessage:"+reason);
        }
    }

}
