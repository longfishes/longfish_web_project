package com.longfish.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatTest {
    private static final String SERVER_ADDRESS = "localhost"; // 服务器地址
    private static final int SERVER_PORT = 12445; // 服务器端口

    public static void main(String[] args) {
        new ChatServer(12445).startServer();
        try {
            // 连接到服务器
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("已连接到服务器");
            // 创建输入输出流
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 启动一个线程来读取服务器的响应
            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println("服务器: " + response);
                    }
                } catch (IOException e) {
                    System.err.println("读取服务器响应时出错: " + e.getMessage());
                }
            }).start();
            // 从控制台读取用户输入并发送到服务器
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);
            }
        } catch (IOException e) {
            System.err.println("无法连接到服务器: " + e.getMessage());
        }
    }
}
