package com.longfish.jclogindemo.task;

import com.longfish.jclogindemo.socket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WebSocketTask {

    @Autowired
    private WebSocketServer webSocketServer;

    @Scheduled(cron = "0/30 * * * * ?")
    public void sendMessageToClient() {
        webSocketServer.boardCastNoticeToAll();
    }

    @Scheduled(cron = "0/3 * * * * ?")
    public void cleanCache() {
        webSocketServer.cleanCache();
    }
}
