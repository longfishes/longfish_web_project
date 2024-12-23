package com.longfish.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;


public class ChatServer {

    private final int port; // 服务器端口
    private final Set<PrintWriter> clientWriters = new HashSet<>(); // 存储所有客户端的输出流
    private ServerSocket serverSocket;
    private ClientHandler handler;

    public ChatServer(int port) {
        this.port = port;
    }

    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("聊天室服务器 " + port + " 已启动...");
                while (!serverSocket.isClosed()) {
                    try {
                        handler = new ClientHandler(serverSocket.accept()); // 接受客户端连接并启动新线程处理
                        handler.start();
                    } catch (IOException e) {
                        if (serverSocket.isClosed()) {
                            System.out.println("服务器已关闭，停止接受新连接。");
                        } else {
                            System.err.println("接受客户端连接时发生异常: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("聊天服务器启动失败: " + e.getMessage());
            }
        }).start();
    }

    public void exit() {
        System.out.println("聊天服务器正在关闭...");
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.close();
                }
                clientWriters.clear();
            }
            if (handler != null && handler.isAlive()) {
                handler.interrupt(); // 中断客户端处理线程
            }
        } catch (IOException e) {
            System.err.println("关闭服务器时发生异常: " + e.getMessage());
        }
        System.out.println("聊天服务器已关闭");
    }

    private class ClientHandler extends Thread {
        private final Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("收到消息: " + message);
                    broadcastMessage(message, out); // 传递当前客户端的输出流
                }
            } catch (IOException ignored) {} finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }

        private void broadcastMessage(String message, PrintWriter senderOut) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    if (writer != senderOut) { // 排除发送者
                        writer.println(message);
                    }
                }
            }
        }
    }

}
