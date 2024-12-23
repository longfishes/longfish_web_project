package com.longfish.network;

import org.junit.Test;

import java.io.*;
import java.net.*;

public class TestClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;

    public TestClient() {
        // 初始化并连接到服务器
        try {
            connectToServer();
            System.out.println("连接到服务器...");
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 连接到主服务器
    private void connectToServer() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
    }

    // 创建房间并返回房间号和端口号
    public void createRoom(String roomName, String description) throws IOException {
        // 发送创建房间的请求
        writer.writeInt(1); // 1 表示创建房间
        writer.writeUTF(roomName);
        writer.writeUTF(description);

        // 读取返回的房间号和端口
        int roomNumber = reader.readInt();
        int port = reader.readInt();

        System.out.println("房间创建成功！");
        System.out.println("房间号：" + roomNumber);
        System.out.println("房间端口号：" + port);
    }

    // 加入房间，传入房间号，获取端口号并连接
    public void joinRoom(int roomNumber) throws IOException {
        // 发送加入房间的请求
        writer.writeInt(2); // 2 表示加入房间
        writer.writeInt(roomNumber); // 传入房间号

        // 获取返回的房间端口号
        int port = reader.readInt();
        System.out.println("成功加入房间 " + roomNumber + "，房间端口：" + port);

        // 可以在这里继续连接该房间的端口进行更多操作，例如发送/接收消息
    }


    @Test
    public void test1() throws IOException, InterruptedException {
        TestClient client = new TestClient();
        client.createRoom("Test Room", "This is a test room.");
        Thread.sleep(10000000);

    }

    @Test
    public void test2() throws IOException, InterruptedException {
        TestClient client = new TestClient();
        client.joinRoom(1);
        Thread.sleep(10000000);
    }

    @Test
    public void test3() throws IOException, InterruptedException {
        TestClient client = new TestClient();
        client.joinRoom(1);
        Thread.sleep(10000000);
    }
}
