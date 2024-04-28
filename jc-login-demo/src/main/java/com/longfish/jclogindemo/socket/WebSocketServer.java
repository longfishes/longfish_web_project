package com.longfish.jclogindemo.socket;

import cn.hutool.core.util.StrUtil;
import com.longfish.jclogindemo.config.WebSocketConfig;
import jakarta.websocket.*;
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
@ServerEndpoint(value = "/ws/{sid}", configurator = WebSocketConfig.class)
public class WebSocketServer {

    private static final Map<String, Integer> cache = new HashMap<>();

    private static final Map<String, Session> sessionMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) throws IOException {
        if (sessionMap.containsKey(sid)) {
            session.getBasicRemote().sendText(sid + " 已在线！");
            session.close();
            return;
        }

        if (sessionMap.size() > 20) {
            session.getBasicRemote().sendText("服务器人太多了，等等再来吧！");
            session.close();
            return;
        }

        String remoteIp = getHeader(session, "x-forwarded-for");

        log.info("{} 客户端 {} 建立连接", remoteIp, sid);
        sessionMap.put(sid, session);
        boardCastNoticeSingle(session);
        sendToOtherClient(session, sid + " 上线了！ 当前在线人数：" + sessionMap.size());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) throws IOException {
        if (message == null || message.equals("")) {
            sessionMap.get(sid).getBasicRemote().sendText("不许发送空白消息！");
            return;
        }

        String remoteIp = getHeader(sessionMap.get(sid), "x-forwarded-for");

        if (cache.containsKey(remoteIp)) {
            cache.put(remoteIp, cache.get(remoteIp) + 1);
        } else {
            cache.put(remoteIp, 1);
        }

        if (cache.get(remoteIp) > 5) {
            sessionMap.get(sid).getBasicRemote().sendText("你发送的太快了！");
            return;
        }

        log.info("{} 客户端 {} 的信息 : {}", remoteIp, sid, message);
        sendToOtherClient(sessionMap.get(sid), sid + " 说 : " + message);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("{} 离线", sid);
        sessionMap.remove(sid);
    }

    @OnError
    public void onError(Throwable e) {
        log.info("{}", e.getMessage());
    }

    public void cleanCache() {
        cache.clear();
    }

    public static String getHeader(Session session, String headerName) throws IOException {
        final String header = (String) session.getUserProperties().get(headerName);
        if (StrUtil.isBlank(header)) {
            log.error("获取header失败，不安全的连接，即将关闭");
            session.close();
        }
        return header;
    }

    public void boardCastNoticeSingle(Session session) throws IOException {
        int cnt = sessionMap.size();
        session.getBasicRemote().sendText("欢迎光临longfish的无聊 websocket 在线聊天室 当前在线人数：" + cnt);
    }

    public void boardCastNoticeToAll() {
        int cnt = sessionMap.size();
        sendToAllClient("欢迎光临longfish的无聊 websocket 在线聊天室 当前在线人数：" + cnt);
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

