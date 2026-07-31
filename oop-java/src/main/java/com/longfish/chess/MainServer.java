package com.longfish.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainServer {

    private static final int PORT = 11111;
    private final Map<Socket, ClientInfo> clients = new ConcurrentHashMap<>();
    private final ServerInstance[] rooms = new ServerInstance[15];
    private final String[] userNames = new String[30];
    private final String[] userAvatars = new String[30];

    public static void main(String[] args) {
        new MainServer().start();
    }

    public void start() {
        for (int i = 0; i < 15; i++) {
            rooms[i] = new ServerInstance(PORT + i + 1);
        }

        for (int i = 0; i < 30; i++) {
            userNames[i] = "no";
        }

        for (int i = 0; i < 30; i++) {
            userAvatars[i] = "no";
        }

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("主服务器启动，等待客户端连接...");

            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientInfo {
        String username;
        String avatar;

        ClientInfo(String username, String avatar) {
            this.username = username;
            this.avatar = avatar;
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // 接收客户端的用户名和头像
                String userInfo = in.readLine();
                String[] parts = userInfo.split("\\|");
                String username = parts[0];
                String avatar = parts[1];
                clients.put(clientSocket, new ClientInfo(username, avatar));
                onConnect(clientSocket);

                String message;
                while ((message = in.readLine()) != null) {
                    onMessage(clientSocket, message);
                }
            } catch (IOException ignored) {} finally {
                removeClient(clientSocket);
            }
        }
    }

    // 发送消息
    private void sendMessage(Socket clientSocket, String message) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            removeClient(clientSocket);
        }
    }

    // 移除客户端
    private void removeClient(Socket clientSocket) {
        if (clients.containsKey(clientSocket)) {
            try {
                onLeave(clientSocket);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(clientSocket);
        }
    }

    // 广播消息（不包括自己）
    private void broadcast(String message, Socket fromSocket) {
        for (Socket clientSocket : clients.keySet()) {
            if (!clientSocket.equals(fromSocket)) {
                sendMessage(clientSocket, message);
            }
        }
    }

    // 广播消息
    private void broadcast(String message) {
        for (Socket clientSocket : clients.keySet()) {
            sendMessage(clientSocket, message);
        }
    }

    // 用户上线
    private void onConnect(Socket clientSocket) {
        ClientInfo info = clients.get(clientSocket);

        // 发送在线用户名
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            names.append(userNames[i]).append(" ");
        }
        sendMessage(clientSocket, names.toString());

        // 发送在线用户头像
        StringBuilder avatars = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            avatars.append(userAvatars[i]).append(" ");
        }
        sendMessage(clientSocket, avatars.toString());

        if (!info.username.equals("admin")) {
            // 发送上线广播
            broadcast("sendMessage:" + info.username + " 上线了");
        }

        System.out.println(info.username + " 上线了");
    }

    //  用户离线
    private void onLeave(Socket clientSocket) {
        ClientInfo info = clients.get(clientSocket);
        String message = info.username + " 离线了";

        if (!info.username.equals("admin")) {
            broadcast("sendMessage:" + message, clientSocket);
        }

        System.out.println(message);
    }

    // 收到消息
    private void onMessage(Socket clientSocket, String message) {
        ClientInfo info = clients.get(clientSocket);
        System.out.println(info.username + " : " + message);

        // 发送消息
        if (message.contains("sendMessage:")) {
            message = message.substring(12);
            broadcast("sendMessage:" + message, clientSocket);
        }

        // 有人加入房间
        if (message.contains("enterRoom:")) {
            message = message.substring(10);
            int roomId = Integer.parseInt(message);
            broadcast("roomEntered:" + roomId + " " + info.username + " " + info.avatar);

            int index = (roomId - 1) * 2;
            if (userNames[index].equals("no")) {

                // 第一个玩家加入
                this.userNames[index] = info.username;
                this.userAvatars[index] = info.avatar;
                rooms[roomId - 1].exit();
                rooms[roomId - 1] = new ServerInstance(PORT + roomId);
                rooms[roomId - 1].start();

            } else if (userNames[index + 1].equals("no")) {

                // 第二个玩家加入
                this.userNames[index + 1] = info.username;
                this.userAvatars[index + 1] = info.avatar;

            }
        }

        // 有人退出房间
        if (message.contains("exitRoom:")) {
            message = message.substring(9);
            int roomId = Integer.parseInt(message);
            int index = (roomId - 1) * 2;
            broadcast("roomExited:" + roomId);
            rooms[roomId - 1].exit();
            userAvatars[index] = "no";
            userNames[index] = "no";
        }

        // 重置房间
        if (message.contains("restartRoom:")) {
            message = message.substring(12);
            int roomId = Integer.parseInt(message);
            broadcast("roomExited:" + roomId);
            rooms[roomId - 1].exit();
            int index = (roomId - 1) * 2;
            userNames[index] = "no";
            userNames[index + 1] = "no";
            userAvatars[index] = "no";
            userAvatars[index + 1] = "no";
        }
    }

}
