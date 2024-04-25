package com.longfish.jclogindemo.socket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    private static final Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) throws IOException {
        log.info("客户端 {} 建立连接", sid);
        if (sessionMap.containsKey(sid)) {
            session.getBasicRemote().sendText("sid 已在线！");
            session.close();
        }
        sessionMap.put(sid, session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到来自客户端 {} 的信息 : {}", sid, message);
        sendToOtherClient(sessionMap.get(sid), sid + " 说 : " + message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("{} 离线", sid);
        sessionMap.remove(sid);
    }

    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendToOtherClient(Session session, String message) {
        sessionMap.keySet().forEach(key -> {
            Session iter = sessionMap.get(key);
            if (!iter.equals(session)) {
                try {
                    iter.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

