package com.longfish.network;

import java.io.*;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;

    public Client() {
        start();
    }

    public void start() {
        try {
            // 连接到服务器
            connectToServer();
            System.out.println("连接到服务器...");

            // 创建输入输出流
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());

            // 创建一个线程用于接收消息
            Thread receiverThread = new Thread(() -> {
                try {
                    while (true) {
                        // 等待接收两个整数
                        int num1 = reader.readInt();
                        int num2 = reader.readInt();
                        System.out.println("从服务器接收到的整数: " + num1 + ", " + num2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiverThread.start();  // 启动接收线程

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 连接到服务器并初始化 socket
    private void connectToServer() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
    }

    // 发送两个整数到服务器
    public void sendNumbers(int num1, int num2) {
        try {
            if (writer != null) {
                writer.writeInt(num1);
                writer.writeInt(num2);
                System.out.println("已发送数据: " + num1 + ", " + num2);
            } else {
                System.out.println("连接尚未建立，无法发送数据");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 启动客户端
        Client client = new Client();
        client.start();

        // 外部调用 sendNumbers 来发送数据
        // 例如模拟发送数据：
        client.sendNumbers(5, 10);
        client.sendNumbers(20, 30);
    }
}
