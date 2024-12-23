package com.longfish.network;

import com.longfish.gomoku.Notice;
import com.longfish.gomoku.RemoteClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RoomInitializer extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private Notice notice;

    public RoomInitializer() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 在窗口关闭时，手动关闭所有 Notice 提示框
                if (notice != null) {
                    notice.close();  // 关闭提示框
                }
                System.exit(0);  // 关闭窗口
            }
        });

        setTitle("联机五子棋");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 设置 CardLayout 布局
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        setResizable(false);

        // 创建主面板（选择加入房间或创建房间）
        JPanel mainPanel = new JPanel();
        JButton createRoomButton = new JButton("创建房间");
        JButton joinRoomButton = new JButton("加入房间");

        createRoomButton.setPreferredSize(new Dimension(200, 300));
        joinRoomButton.setPreferredSize(new Dimension(200, 300));

        createRoomButton.addActionListener(e -> cardLayout.show(cardPanel, "createRoom"));
        joinRoomButton.addActionListener(e -> cardLayout.show(cardPanel, "joinRoom"));

        mainPanel.add(createRoomButton);
        mainPanel.add(joinRoomButton);

        // 创建“创建房间”面板
        JPanel createRoomPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        createRoomPanel.add(new JLabel("棋盘行数:"));
        JTextField rowsField = new JTextField();
        createRoomPanel.add(rowsField);
        rowsField.setText("15");

        createRoomPanel.add(new JLabel("棋盘列数:"));
        JTextField colsField = new JTextField();
        createRoomPanel.add(colsField);
        colsField.setText("15");

        createRoomPanel.add(new JLabel("房间号:"));
        JTextField portFieldCreate = new JTextField();
        createRoomPanel.add(portFieldCreate);

        JButton createButton = new JButton("创建房间");
        createButton.addActionListener(e -> {
            String rows = rowsField.getText();
            String cols = colsField.getText();
            String room = portFieldCreate.getText();
            int x, y;
            try {
                x = Integer.parseInt(rows);
                y = Integer.parseInt(cols);
                if (x < 5 || y < 5 || x > 20 || y > 20) throw new RuntimeException("out of bound!");
                if (room.length() > 10) throw new RuntimeException("too long");
                if (room.equals("")) throw new RuntimeException("empty");
                createRoomInBackground(x, y, room);
            } catch (Exception ex) {
                if (ex.getMessage().equals("too long")) {
                    JOptionPane.showMessageDialog(this, "房间号太长！");
                } else if (ex.getMessage().equals("empty")) {
                    JOptionPane.showMessageDialog(this, "房间号不能为空！");
                }
                else {
                    JOptionPane.showMessageDialog(this, "行列为 5-20 的整数");
                    colsField.setText("15");
                    rowsField.setText("15");
                }
            }

        });
        createRoomPanel.add(createButton);

        JButton backToMainButton1 = new JButton("返回");
        backToMainButton1.addActionListener(e -> cardLayout.show(cardPanel, "main"));
        createRoomPanel.add(backToMainButton1);

        // 创建“加入房间”面板
        JPanel joinRoomPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        joinRoomPanel.add(new JLabel("房间号:"));
        JTextField portFieldJoin = new JTextField();
        joinRoomPanel.add(portFieldJoin);

        JButton joinButton = new JButton("加入房间");
        joinButton.addActionListener(e -> {
            String roomNum = portFieldJoin.getText();
            try {
                if (roomNum.length() > 10) throw new RuntimeException("too long");
                if (roomNum.equals("")) throw new RuntimeException("empty");
                String result = RoomRequests.roomJoin(roomNum);
                if (result.equals("err")) throw new RuntimeException("err");
                joinRoom(Integer.parseInt(result));
            } catch (Exception ex) {
                if (ex.getMessage().equals("too long")) {
                    JOptionPane.showMessageDialog(this, "房间号太长!");
                } else if (ex.getMessage().equals("empty")) {
                    JOptionPane.showMessageDialog(this, "房间号不能为空!");
                } else {
                    JOptionPane.showMessageDialog(this, "房间不存在!");
                }
            }
        });
        joinRoomPanel.add(joinButton);

        JButton backToMainButton2 = new JButton("返回");
        backToMainButton2.addActionListener(e -> cardLayout.show(cardPanel, "main"));
        joinRoomPanel.add(backToMainButton2);

        // 监听回车键
        rowsField.addActionListener(e -> createButton.doClick());
        colsField.addActionListener(e -> createButton.doClick());
        portFieldCreate.addActionListener(e -> createButton.doClick());
        portFieldJoin.addActionListener(e -> joinButton.doClick());

        // 将各个面板添加到 cardPanel 中
        cardPanel.add(mainPanel, "main");
        cardPanel.add(createRoomPanel, "createRoom");
        cardPanel.add(joinRoomPanel, "joinRoom");

        // 默认显示主面板
        cardLayout.show(cardPanel, "main");

        add(cardPanel);
    }

    private void joinRoom(int port) {
        RemoteClient board = new RemoteClient(10, 10, false, port);

        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window != board) {
                window.dispose();
            }
        }
    }

    private void createRoomInBackground(int rows, int cols, String room) {
        RoomInitializer ri = this;
        final int[] port = {-1};
        // 在后台线程中执行创建房间的逻辑
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                String result = RoomRequests.roomCreate(room);
                if (result.equals("err")) JOptionPane.showMessageDialog(null, "房间已存在!");
                else {
                    port[0] = Integer.parseInt(result);
                    Window[] windows = Window.getWindows();
                    for (Window window : windows) {
                        window.dispose();
                    }

                    notice = new Notice(ri);
                    notice.show("正在 " + room + " 房间等待玩家加入...");
                }
                // 在这里不阻塞UI线程
                Thread.sleep(20);
                return null;
            }

            @Override
            protected void done() {
                try {
                    if (port[0] != -1) {
                        final RemoteClient[] board = new RemoteClient[1];
                        new Thread(() -> {
                            board[0] = new RemoteClient(rows, cols, true, port[0]);
                            board[0].setAvailable(true);
                        }).start();

                    } else {
                        RoomInitializer.this.setVisible(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

}
