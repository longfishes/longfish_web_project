package com.longfish.network;

import java.io.*;
import java.net.*;

public class ServerInstance extends Thread {

    private final int port;

    public ServerInstance(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void open() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器启动，等待客户端连接...");

            // 接受两个客户端连接
            Socket client1 = waitForConnection(serverSocket);
            System.out.println("客户端1已连接");
            Socket client2 = waitForConnection(serverSocket);
            System.out.println("客户端2已连接");

            // 创建输入输出流
            DataInputStream reader1 = new DataInputStream(client1.getInputStream());
            DataOutputStream writer1 = new DataOutputStream(client1.getOutputStream());
            DataInputStream reader2 = new DataInputStream(client2.getInputStream());
            DataOutputStream writer2 = new DataOutputStream(client2.getOutputStream());

            // 用于控制交替发送消息
            boolean turnForClient1 = true;

            writer1.writeBoolean(true);

            int x = reader1.readInt();
            int y = reader1.readInt();
            writer2.writeInt(x);
            writer2.writeInt(y);

            // 交替接收和转发消息
            while (true) {
                if (turnForClient1) {
                    // 从客户端1接收消息并转发给客户端2
                    int num1 = reader1.readInt();
                    int num2 = reader1.readInt();
                    System.out.println("客户端1发送的整数: " + num1 + ", " + num2);
                    writer2.writeInt(num1);
                    writer2.writeInt(num2);
                } else {
                    // 从客户端2接收消息并转发给客户端1
                    int num1 = reader2.readInt();
                    int num2 = reader2.readInt();
                    System.out.println("客户端2发送的整数: " + num1 + ", " + num2);
                    writer1.writeInt(num1);
                    writer1.writeInt(num2);
                }

                // 切换发送消息的客户端
                turnForClient1 = !turnForClient1;
            }

        } catch (IOException e) {
            System.exit(-1);
        }
    }

    // 等待客户端连接并返回连接的 Socket
    private Socket waitForConnection(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }

    @Override
    public void run() {
        open();
    }
}
