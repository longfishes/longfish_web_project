package com.longfish.chess;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainServerNio {

    private static final int PORT = 11111;
    private final Map<SocketChannel, ClientInfo> clients = new ConcurrentHashMap<>();
    private final ServerInstance[] rooms = new ServerInstance[15];
    private final String[] userNames = new String[30];
    private final String[] userAvatars = new String[30];
    private final Selector selector;

    public MainServerNio() throws IOException {
        this.selector = Selector.open();
        for (int i = 0; i < 15; i++) {
            rooms[i] = new ServerInstance(PORT + i + 1);
        }
        for (int i = 0; i < 30; i++) {
            userNames[i] = "no";
            userAvatars[i] = "no";
        }
    }

    public static void main(String[] args) {
        try {
            new MainServerNio().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("NIO主服务器启动，等待客户端连接...");

        //noinspection InfiniteLoopStatement
        while (true) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isAcceptable()) {
                    handleAccept(serverSocketChannel);
                } else if (key.isReadable()) {
                    handleRead(key);
                }
            }
        }
    }

    private void handleAccept(ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("客户端连接: " + clientChannel.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        int bytesRead;
        try {
            bytesRead = clientChannel.read(buffer);
        } catch (IOException e) {
            removeClientOnce(clientChannel);
            return;
        }

        if (bytesRead == -1) {
            removeClientOnce(clientChannel);
            return;
        }

        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit()).trim();

        // 处理消息
        if (!clients.containsKey(clientChannel)) {
            // 如果客户端尚未注册,第一条消息是用户名和头像
            String[] parts = message.split("\\|");
            if (parts.length == 2) {
                String username = parts[0];
                String avatar = parts[1];
                clients.put(clientChannel, new ClientInfo(username, avatar));
                onConnect(clientChannel);
            } else {
                System.err.println("注册消息错误: " + message);
            }
        } else {
            String[] messages = message.split("\r");
            for (String msg : messages) {
                onMessage(clientChannel, msg.trim());
            }

        }
    }

    private void onMessage(SocketChannel clientChannel, String message) {
        ClientInfo info = clients.get(clientChannel);
        System.out.println("收到来自 " + info.username + " 的消息 : " + message);

        // 发送消息
        if (message.contains("sendMessage:")) {
            message = message.substring(12);
            broadcast("sendMessage:" + message, clientChannel);
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

    private void onConnect(SocketChannel clientChannel) {
        ClientInfo info = clients.get(clientChannel);

        // 发送在线用户名
        StringBuilder names = new StringBuilder();
        for (String userName : userNames) {
            names.append(userName).append(" ");
        }
        sendMessage(clientChannel, names.toString().trim());

        // 发送在线用户头像
        StringBuilder avatars = new StringBuilder();
        for (String userAvatar : userAvatars) {
            avatars.append(userAvatar).append(" ");
        }
        sendMessage(clientChannel, avatars.toString().trim());

        if (!info.username.equals("admin")) {
            broadcast("sendMessage:" + info.username + " 上线了");
        }

        System.out.println(info.username + " 上线了");
    }

    private void sendMessage(SocketChannel clientChannel, String message) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes(StandardCharsets.UTF_8));
            while (buffer.hasRemaining()) {
                clientChannel.write(buffer);
            }
        } catch (IOException e) {
            removeClient(clientChannel);
        }
    }

    private void broadcast(String message, SocketChannel fromChannel) {
        ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes(StandardCharsets.UTF_8));
        for (SocketChannel clientChannel : clients.keySet()) {
            if (!clientChannel.equals(fromChannel)) {
                try {
                    buffer.rewind(); // 重置缓冲区以便重新写入
                    while (buffer.hasRemaining()) {
                        clientChannel.write(buffer);
                    }
                } catch (IOException e) {
                    removeClient(clientChannel);
                }
            }
        }
    }

    private void broadcast(String message) {
        ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes(StandardCharsets.UTF_8));
        for (SocketChannel clientChannel : clients.keySet()) {
            try {
                buffer.rewind(); // 重置缓冲区以便重新写入
                while (buffer.hasRemaining()) {
                    clientChannel.write(buffer);
                }
            } catch (IOException e) {
                removeClient(clientChannel);
            }
        }
    }

    private void removeClientOnce(SocketChannel clientChannel) {
        try {
            ClientInfo info = clients.get(clientChannel);
            if (info != null) {
                if (!info.username.equals("admin")) {
                    System.out.println(info.username + " 离线了");

                    // 清理用户在房间中的状态
                    for (int i = 0; i < rooms.length; i++) {
                        int index = i * 2;
                        if (userNames[index].equals(info.username)) {
                            userNames[index] = "no";
                            userAvatars[index] = "no";
                        } else if (userNames[index + 1].equals(info.username)) {
                            userNames[index + 1] = "no";
                            userAvatars[index + 1] = "no";
                        }
                    }
                }
            }
            clients.remove(clientChannel);
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient(SocketChannel clientChannel) {
        try {
            ClientInfo info = clients.get(clientChannel);
            if (info != null) {
                if (!info.username.equals("admin")) {
                    System.out.println(info.username + " 离线了");
                    broadcast("sendMessage:" + info.username + " 离线了", clientChannel);

                    // 清理用户在房间中的状态
                    for (int i = 0; i < rooms.length; i++) {
                        int index = i * 2;
                        if (userNames[index].equals(info.username)) {
                            userNames[index] = "no";
                            userAvatars[index] = "no";
                        } else if (userNames[index + 1].equals(info.username)) {
                            userNames[index + 1] = "no";
                            userAvatars[index + 1] = "no";
                        }
                    }
                }
            }
            clients.remove(clientChannel);
            clientChannel.close();
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

}
