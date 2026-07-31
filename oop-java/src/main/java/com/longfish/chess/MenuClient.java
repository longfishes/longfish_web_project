package com.longfish.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MenuClient extends JFrame {

    private static final String projectPath = "gomoku-java-client\\src\\main\\resources\\";
    private JPanel currentSelectedBorderPanel = null; // 用于跟踪当前选中的边框面板
    private int selectedRoom = -1;
    private final JLabel[][] userAvatarLabels = new JLabel[15][2]; // 用于存储用户头像

    private final Socket mainSocket;
    private final PrintWriter out;
    private final BufferedReader in;
    private final String SERVER_ADDRESS;
    private final int SERVER_PORT = 11111;
    private JTextArea messageArea;

    private final String username;
    private final String avatar;
    private int myPosition = -1;

    private String[] userNames;
    private String[] userAvatars;

    public MenuClient() {
        this("localhost", "", "1-1", false);
    }

    public MenuClient(String server, String name, String ava, boolean isAutoLogin) {
        // 登录
        LoginClient.Connection connection;
        if (isAutoLogin) connection = LoginClient.login(server, name, ava);
        else connection = LoginClient.showLoginDialog(server, name, ava);
        SERVER_ADDRESS = connection.serverAddr();
        mainSocket = connection.socket();
        in = connection.in();
        out = connection.out();
        username = connection.username();
        avatar = connection.avatar();

        setTitle("联机大厅");
        setSize(600 + 200, 680);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (myPosition != -1) out.println("exitRoom:" + (myPosition / 2 + 1));
                dispose();
                new Thread(() -> new MenuClient(SERVER_ADDRESS, username, avatar, false)).start();
            }
        });
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(81, 113, 158));

        // 添加顶部间隔
        JPanel topSpacer = new JPanel();
        topSpacer.setPreferredSize(new Dimension(600, 25));
        topSpacer.setBackground(new Color(81, 113, 158));
        mainPanel.add(topSpacer, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(5, 3, 10, 10));
        gridPanel.setBackground(new Color(81, 113, 158));

        for (int i = 1; i <= 15; i++) {
            JPanel roomPanel = new JPanel();
            roomPanel.setLayout(new BorderLayout());
            roomPanel.setBackground(new Color(81, 113, 158));

            JLabel roomLabel = new JLabel("- " + i + " -", SwingConstants.CENTER);
            roomLabel.setForeground(Color.WHITE);
            roomLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            roomLabel.setPreferredSize(new Dimension(100, 50));
            roomPanel.add(roomLabel, BorderLayout.SOUTH);

            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            centerPanel.setBackground(new Color(81, 113, 158));

            // 使用 JLayeredPane 来管理边框和内容
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(160, 60)); // 增加大小以容纳边框

            // 新的面板来包裹座位和头像
            JPanel seatAndAvatarsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            seatAndAvatarsPanel.setBackground(new Color(81, 113, 158));
            seatAndAvatarsPanel.setBounds(5, 5, 150, 50); // 设置位置和大小，留出边框空间

            JLabel leftAvatar = new JLabel(new ImageIcon(new ImageIcon(projectPath + "img\\noone.gif")
                    .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            seatAndAvatarsPanel.add(leftAvatar);
            userAvatarLabels[i - 1][0] = leftAvatar; // 存储左侧头像

            // seat
            ImageIcon seatIconImage = new ImageIcon(new ImageIcon(projectPath + "img\\xqnoone.gif")
                    .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            JLabel seatIcon = new JLabel(seatIconImage);
            seatAndAvatarsPanel.add(seatIcon);

            JLabel rightAvatar = new JLabel(new ImageIcon(new ImageIcon(projectPath + "img\\noone.gif")
                    .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            seatAndAvatarsPanel.add(rightAvatar);
            userAvatarLabels[i - 1][1] = rightAvatar; // 存储右侧头像

            // 边框面板
            JPanel borderPanel = new JPanel();
            borderPanel.setBounds(0, 0, 160, 60); // 设置位置和大小
            borderPanel.setOpaque(false); // 透明背景

            // 添加点击监听器
            int finalI = i;
            MouseAdapter clickListener = new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    // 如果有已选中的房间，先清除其边框
                    if (currentSelectedBorderPanel != null) {
                        currentSelectedBorderPanel.setBorder(null);
                    }
                    // 渲染当前选中房间的边框
                    borderPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                    // 更新当前选中的房间
                    currentSelectedBorderPanel = borderPanel;
                    selectedRoom = finalI;
                }
            };

            seatIcon.addMouseListener(clickListener);
            leftAvatar.addMouseListener(clickListener);
            rightAvatar.addMouseListener(clickListener);

            layeredPane.add(seatAndAvatarsPanel, JLayeredPane.DEFAULT_LAYER);
            layeredPane.add(borderPanel, JLayeredPane.PALETTE_LAYER);

            centerPanel.add(layeredPane);
            roomPanel.add(centerPanel, BorderLayout.CENTER);
            gridPanel.add(roomPanel);
        }

        // 北侧面板，展示房间信息和按钮
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // 房间信息和按钮面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(10, 26)); // 设置固定高度
        JLabel roomInfoLabel = new JLabel("<<< 象棋游戏 >>>", SwingConstants.LEFT);
        infoPanel.add(roomInfoLabel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton joinButton = new JButton("加入");
        joinButton.addActionListener(l -> {
            if (selectedRoom == -1) {
                JOptionPane.showMessageDialog(this, "请选择房间！");
                return;
            }
            int index = (selectedRoom - 1) * 2;
            if (!userAvatars[index].equals("no") && !userAvatars[index + 1].equals("no")) {
                JOptionPane.showMessageDialog(this, "房间已满！");
                return;
            }
            if (myPosition == index) {
                return;
            }

            if (myPosition != -1) {
                out.println("exitRoom:" + (myPosition / 2 + 1));
                myPosition = index;
            }

            out.println("enterRoom:" + selectedRoom);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
            myPosition = index;

            new Thread(() ->
                    new RemoteClient(
                    userNames[index + 1].equals("no"),
                    SERVER_ADDRESS,
                    SERVER_PORT + selectedRoom,
                    selectedRoom,
                    username,
                    avatar
            )).start();
        });

        JButton exitButton = new JButton("退出");
        exitButton.addActionListener(l -> {
            if (myPosition != -1) out.println("exitRoom:" + (myPosition / 2 + 1));
            dispose();
            new Thread(() -> new MenuClient(SERVER_ADDRESS, username, avatar, false)).start();
        });

        buttonPanel.add(joinButton);
        buttonPanel.add(exitButton);
        infoPanel.add(buttonPanel, BorderLayout.EAST);

        topPanel.add(infoPanel, BorderLayout.NORTH);

        // 添加间距
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(600, 25));
        spacer.setBackground(new Color(81, 113, 158));
        topPanel.add(spacer, BorderLayout.SOUTH);

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 添加右侧面板
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(200, 680));
        rightPanel.setBackground(Color.WHITE);

        // 个人信息面板
        JPanel personalInfoPanel = createUserInfoPanel(avatar + ".gif", username);
        rightPanel.add(personalInfoPanel);

        // 服务器信息面板（作为消息框）
        JPanel serverInfoPanel = createServerInfoPanel();
        rightPanel.add(serverInfoPanel);

        add(rightPanel, BorderLayout.EAST);
        add(mainPanel);

        setVisible(true);

        // 启动服务器通信线程
        new Thread(this::connectToServer).start();
    }

    private void onMessage(String message) {
        // 发送消息
        if (message.contains("sendMessage:")) {
            message = message.substring(12);
            addTextToMessageArea(message);
        }

        // 有人加入房间
        if (message.contains("roomEntered:")) {
            message = message.substring(12);
            String[] vars = message.split(" ");
            int roomId = Integer.parseInt(vars[0]);
            String username = vars[1];
            String avatar = vars[2];
            int firstIndex = (roomId - 1) * 2;
            int secondIndex = (roomId - 1) * 2 + 1;
            if (userAvatars[firstIndex].equals("no")) {
                updateUserAvatar(firstIndex, avatar);
                userNames[firstIndex] = username;
            } else if (userAvatars[secondIndex].equals("no")) {
                updateUserAvatar(secondIndex, avatar);
                userNames[secondIndex] = username;
            }
        }

        // 有人退出房间
        if (message.contains("roomExited:")) {
            message = message.substring(11);
            int roomId = Integer.parseInt(message);
            int index = (roomId - 1) * 2;
            updateUserAvatar(index, "no");
            updateUserAvatar(index + 1, "no");
        }

    }

    private void connectToServer() {
        try {
            // 读取在线用户名
            userNames = in.readLine().split(" ");

            // 读取在线用户头像
            String[] userAvatars = in.readLine().split(" ");
            this.userAvatars = userAvatars;
            for (int i = 0; i < userAvatars.length; i++) {
                updateUserAvatar(i, userAvatars[i]);
            }

            // 接收服务器的响应（如果有）
            String response;
            while ((response = in.readLine()) != null) {
                onMessage(response);
            }
        } catch (IOException ignored) {
            JOptionPane.showMessageDialog(this, "服务器已离线！");
            backToLogin();
        }
    }

    private void backToLogin() {
        try {
            mainSocket.close();
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                window.dispose();
            }
        } catch (IOException ignore) {}
        new MenuClient(SERVER_ADDRESS, username, avatar, false);
    }

    // 修改用户头像的方法
    public void updateUserAvatar(int avatarIndex, String face) {
        if (avatarIndex >= 0 && avatarIndex < 30) {
            int roomIndex = avatarIndex / 2;
            int positionIndex = avatarIndex % 2;
            String facePath;
            if (face.equals("no")) facePath = projectPath + "img\\noone.gif";
            else facePath = projectPath + "face\\" + face + ".gif";
            ImageIcon newIcon = new ImageIcon(new ImageIcon(facePath)
                    .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            userAvatarLabels[roomIndex][positionIndex].setIcon(newIcon);
            userAvatars[avatarIndex] = face;
        } else {
            System.out.println("Invalid avatar index: " + avatarIndex);
        }
    }

    // 创建用户信息面板
    private JPanel createUserInfoPanel(String avatarName, String userName) {
        ImageIcon avatar = new ImageIcon(projectPath + "face\\" + avatarName);
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BorderLayout());

        // 使用 createMenuLabel 创建标题
        JLabel titleLabel = createMenuLabel("个人信息");
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE); // 设置背景色为白色
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 设置上下5像素，左右10像素的间距
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        userInfoPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel userDetailPanel = new JPanel();
        userDetailPanel.setLayout(new BoxLayout(userDetailPanel, BoxLayout.Y_AXIS));
        userDetailPanel.setBackground(Color.WHITE);

        // 添加额外的图片
        ImageIcon extraImage = new ImageIcon(projectPath + "img\\boy1.gif");
        JLabel extraImageLabel = new JLabel(extraImage);
        extraImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        extraImageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // 添加上下间距
        userDetailPanel.add(extraImageLabel);

        // 创建一个水平面板来放置头像和用户名
        JPanel horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // 居中对齐
        horizontalPanel.setBackground(Color.WHITE);

        // 添加头像图片
        JLabel avatarLabel = new JLabel(avatar);
        horizontalPanel.add(avatarLabel);

        // 添加用户名
        JLabel nameLabel = new JLabel(userName);
        horizontalPanel.add(nameLabel);

        userDetailPanel.add(horizontalPanel);

        userInfoPanel.add(userDetailPanel, BorderLayout.CENTER);

        return userInfoPanel;
    }

    // 创建服务器信息面板（作为消息框）
    private JPanel createServerInfoPanel() {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBackground(Color.WHITE); // 设置背景为白色

        // 使用 createMenuLabel 创建标题
        JLabel titleLabel = createMenuLabel("服务器信息");
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE); // 设置背景色为白色
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 设置上下5像素，左右10像素的间距
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        messagePanel.add(titlePanel, BorderLayout.NORTH);

        // 创建消息框
        messageArea = new JTextArea();
        messageArea.setEditable(false); // 设置为不可编辑
        messageArea.setLineWrap(true); // 自动换行
        messageArea.setWrapStyleWord(true); // 以单词为单位换行
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY), // 外边框
            BorderFactory.createEmptyBorder(0, 0, 0, 0) // 内边距
        ));

        // 将消息框放入滚动面板中
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setPreferredSize(new Dimension(100, 200));

        // 创建一个中间面板来设置背景和间距
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE); // 设置背景为白色
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10)); // 添加四周间距
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        messagePanel.add(centerPanel, BorderLayout.CENTER);

        // 创建输入框和发送按钮
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE); // 设置背景为白色
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10)); // 添加四周间距

        JTextField inputField = new JTextField();
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY), // 外边框
            BorderFactory.createEmptyBorder(5, 5, 5, 5) // 内边距
        ));

        JButton sendButton = new JButton("发送");

        // 添加回车键监听器
        inputField.addActionListener(e -> sendButton.doClick());

        sendButton.addActionListener(l -> {
            String input = inputField.getText();
            if (!input.equals("")) {
                addTextToMessageArea(username + "：" + input);
                out.println("sendMessage:" + username + "：" + input);
                inputField.setText("");
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        messagePanel.add(inputPanel, BorderLayout.SOUTH);

        return messagePanel;
    }

    // 添加文本到消息框
    private void addTextToMessageArea(String text) {
        if (messageArea != null) {
            messageArea.append(text + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength()); // 滚动到最新消息
        }
    }

    // 创建菜单标签
    private JLabel createMenuLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true); // 使背景颜色可见
        label.setBackground(new Color(240, 240, 240)); // 设置背景颜色
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY), // 外边框
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // 内边距：上下5像素，左右10像素
        ));
        label.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        label.setPreferredSize(new Dimension(180, 30)); // 设置首选大小
        return label;
    }

    public static void main(String[] args) {
        new MenuClient();
    }
}
