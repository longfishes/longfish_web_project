package com.longfish.chess;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerInstance extends Thread {

    private final int port;
    private ServerSocket serverSocket;
    private Socket client1;
    private Socket client2;
    private DataInputStream reader1;
    private DataOutputStream writer1;
    private DataInputStream reader2;
    private DataOutputStream writer2;
    private final ChatServer chatServer;

    public ServerInstance(int port) {
        this.port = port;
        chatServer = new ChatServer(port + 100);
    }

    public void exit() {
        chatServer.exit();
        try {
            if (client1 != null && !client1.isClosed()) {
                client1.close();
                reader1.close();
                writer1.close();
            }
            if (client2 != null && !client2.isClosed()) {
                client2.close();
                reader2.close();
                writer2.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            interrupt();
            System.out.println(port + " server stopped!");
        } catch (Exception e) {
            System.out.println(port + " server stopped!");
            interrupt();
        }
    }

    public void open() {
        try {
            chatServer.startServer();
            serverSocket = new ServerSocket(port);
            System.out.println("服务器 " + port + " 启动，等待客户端连接...");

            // 接受两个客户端连接
            client1 = waitForConnection(serverSocket);
            System.out.println("客户端1已连接");
            client2 = waitForConnection(serverSocket);
            System.out.println("客户端2已连接");

            // 创建输入输出流
            reader1 = new DataInputStream(client1.getInputStream());
            writer1 = new DataOutputStream(client1.getOutputStream());
            reader2 = new DataInputStream(client2.getInputStream());
            writer2 = new DataOutputStream(client2.getOutputStream());

            // 用于控制交替发送消息
            boolean turnForClient1 = true;

            writer1.writeBoolean(true);

            String name = reader1.readUTF();
            String face = reader1.readUTF();
            writer2.writeUTF(name);
            writer2.writeUTF(face);
            String name2 = reader2.readUTF();
            String face2 = reader2.readUTF();
            writer1.writeUTF(name2);
            writer1.writeUTF(face2);

            // 交替接收和转发消息
            //noinspection InfiniteLoopStatement
            while (true) {
                if (turnForClient1) {
                    // 从客户端1接收消息并转发给客户端2
                    int num1 = reader1.readInt();
                    int num2 = reader1.readInt();
                    int num3 = reader1.readInt();
                    int num4 = reader1.readInt();
                    System.out.println("客户端1发送的整数: " + num1 + ", " + num2 + ", " + num3 + ", " + num4);
                    writer2.writeInt(num1);
                    writer2.writeInt(num2);
                    writer2.writeInt(num3);
                    writer2.writeInt(num4);
                    afterSend(num1, num2, num3, num4);
                } else {
                    // 从客户端2接收消息并转发给客户端1
                    int num1 = reader2.readInt();
                    int num2 = reader2.readInt();
                    int num3 = reader2.readInt();
                    int num4 = reader2.readInt();
                    System.out.println("客户端2发送的整数: " + num1 + ", " + num2 + ", " + num3 + ", " + num4);
                    writer1.writeInt(num1);
                    writer1.writeInt(num2);
                    writer1.writeInt(num3);
                    writer1.writeInt(num4);
                    afterSend(num1, num2, num3, num4);
                }

                // 切换发送消息的客户端
                turnForClient1 = !turnForClient1;
            }

        } catch (IOException e) {
            System.out.println(port + " server stopped!");
            exit();
        }
    }

    private void afterSend(int num1, int num2, int num3, int num4) {
        if (num1 == -1) {
            if (num2 == -4 && num3 == -4 && num4 == -4) {
                resetRoom();
                exit();
            } else if (num2 == -2 && num3 == -1 && num4 == -2) {
                resetRoom();
                exit();
            }
        }
    }

    private void resetRoom() {
        // 重置房间
        int roomId = port - 11111;
        try (Socket socket = new Socket("localhost", 11111)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 发送用户名和头像信息
            String username = "admin";
            String avatar = "admin";
            out.println(username + "|" + avatar);

            // 发送一条消息
            String message = "restartRoom:" + roomId;
            out.println(message);

        } catch (IOException e) {
            e.printStackTrace();
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

    public static void main(String[] args) throws InterruptedException {
        ServerInstance instance = new ServerInstance(12345);
        instance.start();

        Thread.sleep(10);
        System.out.println();

        instance.exit();

        Thread.sleep(10);
        System.out.println();

        new ServerInstance(12345).start();
    }
}
