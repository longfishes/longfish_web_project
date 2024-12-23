package com.longfish.chess;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class LoginClient extends JFrame {

    private final JTextField usernameField;
    private final JTextField serverField;
    private final JLabel selectedAvatarLabel;
    private String selectedAvatar;
    private static final String facePath = "gomoku-java-client/src/main/resources/face";
    private static final String defaultServerIp = "localhost";
    private static final int defaultServerPort = 11111;

    private Connection connection;

    public LoginClient(String server, String username, String avatar) {
        setTitle("登录窗口");
        setSize(480, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField();
        usernameField.setText(username);
        inputPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("服务器:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        serverField = new JTextField(server);
        inputPanel.add(serverField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("头像:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        selectedAvatarLabel = new JLabel();
        inputPanel.add(selectedAvatarLabel, gbc);

        add(inputPanel, BorderLayout.NORTH);

        JPanel avatarPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        selectedAvatar = avatar;
        File avatarDir = new File(facePath);
        int x = 0, y = 0;
        for (File file : Objects.requireNonNull(avatarDir.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".gif")) {
                ImageIcon icon = new ImageIcon(file.getPath());
                JButton avatarButton = new JButton(icon);
                avatarButton.setPreferredSize(new Dimension(30, 30));
                avatarButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                avatarButton.addActionListener(e -> {
                    selectedAvatar = file.getName();
                    selectedAvatarLabel.setIcon(icon);
                });

                gbc.gridx = x;
                gbc.gridy = y;
                avatarPanel.add(avatarButton, gbc);

                x++;
                if (x >= 11) {
                    x = 0;
                    y++;
                }

                if (file.getName().equals(avatar + ".gif")) {
                    selectedAvatar = file.getName();
                    selectedAvatarLabel.setIcon(icon);
                }
            }
        }

        add(new JScrollPane(avatarPanel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton connectButton = new JButton("连接");
        JButton resetButton = new JButton("重置");
        JButton exitButton = new JButton("退出");

        connectButton.addActionListener(e -> connect());
        resetButton.addActionListener(e -> reset());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(connectButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static Connection login(String server, String username, String avatar) {
        LoginClient loginClient = new LoginClient(server, username, avatar);
        loginClient.connect();

        return loginClient.connection;
    }

    public static Connection showLoginDialog(String server, String username, String avatar) {
        LoginClient loginClient = new LoginClient(server, username, avatar);
        loginClient.setVisible(true);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (loginClient) {
            try {
                loginClient.wait();
            } catch (InterruptedException ignored) {}
        }

        return loginClient.connection;
    }

    private void connect() {
        String username = usernameField.getText();
        String server = serverField.getText();
        if (username.equals("")) {
            JOptionPane.showMessageDialog(this, "请输入用户名");
        } else {
            connection = enterMenu(server, username, selectedAvatar.substring(0, selectedAvatar.lastIndexOf('.')));
            if (connection == null) {
                JOptionPane.showMessageDialog(this, "服务器离线！");
                return;
            }
            synchronized (this) {
                notify(); // 通知等待的线程
            }
        }
    }

    private Connection enterMenu(String server, String username, String selectedAvatar) {
        try {
            Socket socket = new Socket(server, defaultServerPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username + "|" + selectedAvatar);

            dispose();
            return new Connection(server, socket, out, in, username, selectedAvatar);
        } catch (IOException ignored) {
            return null;
        }
    }

    public record Connection(String serverAddr,
                             Socket socket,
                             PrintWriter out,
                             BufferedReader in,
                             String username,
                             String avatar) {}

    private void reset() {
        usernameField.setText("");
        serverField.setText(defaultServerIp);
        selectedAvatarLabel.setIcon(new ImageIcon(facePath + "/1-1.gif"));
        selectedAvatar = "1-1.gif";
    }

}
