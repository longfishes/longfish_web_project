package com.longfish.gomoku;

import com.longfish.logic.Logic;
import com.longfish.network.RoomInitializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RemoteClient extends JFrame {
    private int BOARD_WIDTH;  // 棋盘宽度（列数）
    private int BOARD_HEIGHT; // 棋盘高度（行数）
    private static final int CELL_SIZE = 40;    // 每个格子的大小
    private final int BOARD_SIZE_X; // 棋盘总宽度
    private final int BOARD_SIZE_Y; // 棋盘总高度
    private final Boolean[][] board; // 棋盘状态，true为黑子，false为白子
    private boolean isBlackTurn = true; // 黑白交替，true为黑子，false为白子
    private boolean available = false;
    private final boolean isHolder;
    private int lastX = -1, lastY = -1; // 存储最后一个落子的位置


    private static final String SERVER_ADDRESS = "k.longfish.site";
    private final int SERVER_PORT;
    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;

    public RemoteClient(int width, int height, boolean isHolder, int port) {
        this.SERVER_PORT = port;
        this.isHolder = isHolder;
        BOARD_WIDTH = width;
        BOARD_HEIGHT = height;
        start();
        BOARD_SIZE_X = BOARD_WIDTH * CELL_SIZE;
        BOARD_SIZE_Y = BOARD_HEIGHT * CELL_SIZE;
        setTitle("五子棋");
        setSize(BOARD_SIZE_X + 15, BOARD_SIZE_Y + 35); // 调整窗口大小以适应棋盘
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    window.dispose();
                }
                SwingUtilities.invokeLater(() -> new RoomInitializer().setVisible(true));
            }
        });
        setLocationRelativeTo(null); // 居中显示窗口
        setResizable(false);         // 禁用窗口大小调整

        board = new Boolean[BOARD_WIDTH][BOARD_HEIGHT]; // 初始化棋盘
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                board[i][j] = null; // 空位置
            }
        }

        GomokuPanel panel = new GomokuPanel();
        add(panel);
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            window.dispose();
        }
        setVisible(true);
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void placePiece(int x, int y) {
        if (x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT && board[x][y] == null) {
            board[x][y] = isBlackTurn;
            lastX = x;
            lastY = y;
            isBlackTurn = !isBlackTurn;
            repaint();
            checkWin();
        } else {
            throw new RuntimeException("无效的落子位置！");
        }
    }

    private void sendPos(int x, int y) {
        sendNumbers(x, y);
        available = false;
    }

    private class GomokuPanel extends JPanel {
        private int hoverX = -1, hoverY = -1; // 存储鼠标悬浮的位置

        public GomokuPanel() {
            setPreferredSize(new Dimension(BOARD_SIZE_X + 40, BOARD_SIZE_Y + 40));

            // 监听鼠标点击事件
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (available) {
                        // 获取点击的棋盘坐标
                        int x = (e.getX()) / CELL_SIZE; // x坐标
                        int y = (e.getY()) / CELL_SIZE; // y坐标

                        // 如果点击的是棋盘的交叉点且该位置没有棋子，则落子
                        if (x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT && board[x][y] == null) {

                            sendPos(x, y);

                            board[x][y] = isBlackTurn;
                            isBlackTurn = !isBlackTurn; // 切换下一回合的玩家
                            hoverX = -1; // 取消显示提示框
                            hoverY = -1;
                            lastX = x;
                            lastY = y;
                            repaint(); // 重新绘制棋盘
                            checkWin();
                        }
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (available) {
                        int x = (e.getX()) / CELL_SIZE;
                        int y = (e.getY()) / CELL_SIZE;

                        if (x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT && board[x][y] == null) {
                            hoverX = x;
                            hoverY = y;
                        } else {
                            hoverX = -1;
                            hoverY = -1;
                        }
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 绘制棋盘背景
            g.setColor(new Color(255, 204, 102)); // 棋盘颜色
            g.fillRect(0, 0, BOARD_SIZE_X + 100, BOARD_SIZE_Y + 40);

            // 偏移量，设置棋盘格子整体向右和向下偏移
            int offsetX = 20; // 向右偏移20像素
            int offsetY = 20; // 向下偏移20像素

            // 绘制棋盘格子
            g.setColor(Color.BLACK);
            for (int i = 0; i <= BOARD_HEIGHT; i++) {
                // 绘制横线
                g.drawLine(offsetX, offsetY + i * CELL_SIZE, offsetX + (BOARD_WIDTH - 1) * CELL_SIZE, offsetY + i * CELL_SIZE);
            }
            for (int i = 0; i < BOARD_WIDTH; i++) {
                // 绘制竖线
                g.drawLine(offsetX + i * CELL_SIZE, offsetY, offsetX + i * CELL_SIZE, offsetY + (BOARD_HEIGHT - 1) * CELL_SIZE);
            }

            // 为标准棋盘绘制定位黑点
            if (BOARD_WIDTH == 15 && BOARD_HEIGHT == 15) {
                // 转换为Graphics2D，启用抗锯齿
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制中心和四角的黑点
                int[] xCoords = {3, 7, 11, 3, 11};
                int[] yCoords = {3, 7, 3, 11, 11};

                // 设置点的颜色为黑色
                g2d.setColor(Color.BLACK);

                // 绘制圆点
                for (int i = 0; i < 5; i++) {
                    int x = offsetX + xCoords[i] * CELL_SIZE;
                    int y = offsetY + yCoords[i] * CELL_SIZE;
                    g2d.fillOval(x - 5, y - 5, 10, 10); // 半径为5的圆，确保点大小适中
                }
            }

            // 绘制棋子
            for (int i = 0; i < BOARD_WIDTH; i++) {
                for (int j = 0; j < BOARD_HEIGHT; j++) {
                    if (board[i][j] != null) {
                        // 计算棋子位置
                        int x = offsetX + i * CELL_SIZE - CELL_SIZE / 2 + 5;
                        int y = offsetY + j * CELL_SIZE - CELL_SIZE / 2 + 5;
                        int size = CELL_SIZE - 10;

                        // 创建阴影效果
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // 绘制阴影 (稍微偏移位置，使用灰色或黑色并设置透明度)
                        g2d.setColor(new Color(0, 0, 0, 50)); // 半透明黑色阴影
                        g2d.fillOval(x + 2, y + 2, size, size); // 阴影略微偏移

                        // 绘制棋子
                        if (board[i][j]) {
                            g.setColor(Color.BLACK); // 黑子
                        } else {
                            g.setColor(Color.WHITE); // 白子
                        }
                        g2d.fillOval(x, y, size, size); // 绘制棋子
                    }
                }
            }

            // 如果有最后一个落子，绘制红点
            if (lastX >= 0 && lastY >= 0) {
                g.setColor(Color.RED);
                int centerX = offsetX + lastX * CELL_SIZE + CELL_SIZE / 2;
                int centerY = offsetY + lastY * CELL_SIZE + CELL_SIZE / 2;
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.fillOval(centerX - 25, centerY - 25, 10, 10);   // 红点大小为10x10

            }

            // 显示鼠标悬浮位置
            if (hoverX >= 0 && hoverY >= 0) {
                g.setColor(new Color(0, 0, 0, 80));
                int offsetCircleX = -15;
                int offsetCircleY = -15;
                int diameter = CELL_SIZE - 10;
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.fillOval(offsetX + hoverX * CELL_SIZE + offsetCircleX,
                        offsetY + hoverY * CELL_SIZE + offsetCircleY,
                        diameter, diameter);
            }
        }
    }


    public void start() {
        try {
            // 连接到服务器
            connectToServer();
            System.out.println("连接到服务器...");

            // 创建输入输出流
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());

            if (isHolder) {
                if (reader.readBoolean())
                    sendNumbers(this.BOARD_WIDTH, this.BOARD_HEIGHT);
            }
            else {
                BOARD_WIDTH = reader.readInt();
                BOARD_HEIGHT = reader.readInt();
            }

            // 创建一个线程用于接收消息
            Thread receiverThread = new Thread(() -> {
                try {
                    while (true) {
                        // 等待接收两个整数
                        int num1 = reader.readInt();
                        int num2 = reader.readInt();
                        placePiece(num1, num2);
                        available = true;
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

    private void checkWin() {
        Boolean result = Logic.checkWin(board);
        if (result != null) {
            if (result == isHolder)
                JOptionPane.showMessageDialog(this, "你赢了", "提示", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(this, "你寄了", "提示", JOptionPane.INFORMATION_MESSAGE);

            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                window.dispose();
            }
            SwingUtilities.invokeLater(() -> new RoomInitializer().setVisible(true));
        }
    }


}
