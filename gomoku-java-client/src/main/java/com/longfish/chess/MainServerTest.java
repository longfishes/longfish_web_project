package com.longfish.chess;

import java.io.*;
import java.net.*;

public class MainServerTest {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 11111;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 发送用户名和头像信息
            String username = "testUser";
            String avatar = "testAvatar";
            out.println(username + "|" + avatar);

            // 发送一条消息
            String message = "Hello, this is a test message!";
            out.println(message);

            // 接收服务器的响应（如果有）
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("收到服务器的响应: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 