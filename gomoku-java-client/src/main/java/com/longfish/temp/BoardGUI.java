package com.longfish.temp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardGUI extends JFrame {
    private static int BOARD_WIDTH;  // 棋盘宽度（列数）
    private static int BOARD_HEIGHT; // 棋盘高度（行数）
    private static final int CELL_SIZE = 40;    // 每个格子的大小
    private static int BOARD_SIZE_X; // 棋盘总宽度
    private static int BOARD_SIZE_Y; // 棋盘总高度
    private final Boolean[][] board; // 棋盘状态，true为黑子，false为白子
    private boolean isBlackTurn = true; // 黑白交替，true为黑子，false为白子
    private boolean available = false;

    public BoardGUI(int width, int height) {
        BOARD_WIDTH = width;
        BOARD_HEIGHT = height;
        BOARD_SIZE_X = BOARD_WIDTH * CELL_SIZE;
        BOARD_SIZE_Y = BOARD_HEIGHT * CELL_SIZE;
        setTitle("五子棋");
        setSize(BOARD_SIZE_X + 15, BOARD_SIZE_Y + 35); // 调整窗口大小以适应棋盘
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        setVisible(true);
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setBoard(Boolean[][] newBoard) {
        if (newBoard.length == BOARD_WIDTH && newBoard[0].length == BOARD_HEIGHT) {
            for (int i = 0; i < BOARD_WIDTH; i++) {
                System.arraycopy(newBoard[i], 0, board[i], 0, BOARD_HEIGHT);
            }
            int blackCount = 0;
            int whiteCount = 0;
            for (int i = 0; i < BOARD_WIDTH; i++) {
                for (int j = 0; j < BOARD_HEIGHT; j++) {
                    if (board[i][j] != null) {
                        if (board[i][j]) {
                            blackCount++;
                        } else {
                            whiteCount++;
                        }
                    }
                }
            }
            isBlackTurn = blackCount <= whiteCount;
            repaint();
        } else {
            throw new RuntimeException("棋盘不一致!");
        }
    }

    public void placePiece(int x, int y) {
        if (x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT && board[x][y] == null) {
            board[x][y] = isBlackTurn;
            isBlackTurn = !isBlackTurn;
            repaint();
        } else {
            throw new RuntimeException("无效的落子位置！");
        }
    }

    private void sendPos(int x, int y) {
        System.out.println("x = " + x + "\ty = " + y);
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
                            repaint(); // 重新绘制棋盘
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

            // 绘制棋盘格子（避免越界多画线）
            g.setColor(Color.BLACK);
            for (int i = 0; i <= BOARD_HEIGHT; i++) {
                // 绘制横线
                g.drawLine(offsetX, offsetY + i * CELL_SIZE, offsetX + (BOARD_WIDTH - 1) * CELL_SIZE, offsetY + i * CELL_SIZE);
            }
            for (int i = 0; i < BOARD_WIDTH; i++) {
                // 绘制竖线
                g.drawLine(offsetX + i * CELL_SIZE, offsetY, offsetX + i * CELL_SIZE, offsetY + (BOARD_HEIGHT - 1) * CELL_SIZE);
            }

            // 绘制棋子
            for (int i = 0; i < BOARD_WIDTH; i++) {
                for (int j = 0; j < BOARD_HEIGHT; j++) {
                    if (board[i][j] != null) {
                        // 判断当前是黑子还是白子
                        if (board[i][j]) {
                            g.setColor(Color.BLACK); // 黑子
                        } else {
                            g.setColor(Color.WHITE); // 白子
                        }
                        // 绘制棋子，落子在交叉点的中心
                        g.fillOval(offsetX + i * CELL_SIZE - CELL_SIZE / 2 + 5, offsetY + j * CELL_SIZE - CELL_SIZE / 2 + 5,
                                CELL_SIZE - 10, CELL_SIZE - 10);
                    }
                }
            }

            if (hoverX >= 0 && hoverY >= 0) {
                g.setColor(new Color(0, 0, 0, 80));
                int offsetCircleX = -15;
                int offsetCircleY = -15;
                int diameter = CELL_SIZE - 10;
                g.fillOval(offsetX + hoverX * CELL_SIZE + offsetCircleX,
                        offsetY + hoverY * CELL_SIZE + offsetCircleY,
                        diameter, diameter);
            }
        }
    }


}
