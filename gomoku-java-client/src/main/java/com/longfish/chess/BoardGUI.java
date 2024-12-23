package com.longfish.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class BoardGUI extends JFrame {
    private static final int BOARD_WIDTH = 9;  // 棋盘宽度（列数）
    private static final int BOARD_HEIGHT = 10; // 棋盘高度（行数）
    private static final int CELL_SIZE = 60;    // 每个格子的大小
    private static final int BOARD_SIZE_X = BOARD_WIDTH * CELL_SIZE; // 棋盘总宽度
    private static final int BOARD_SIZE_Y = BOARD_HEIGHT * CELL_SIZE; // 棋盘总高度
    private static final String projectPath = "gomoku-java-client\\src\\main\\resources\\";
    private final boolean isRed;

    public BoardGUI(boolean isRed) {
        this.isRed = isRed;
        setTitle("中国象棋");
        setSize(BOARD_SIZE_X + 40, BOARD_SIZE_Y + 60); // 调整窗口大小以适应棋盘
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示窗口
        setResizable(false);         // 禁用窗口大小调整
        ChessPanel panel = new ChessPanel(isRed);
        add(panel);
        setVisible(true);
    }

    private class ChessPanel extends JPanel {
        private final Image backgroundImage;
        private final Image selectImage;
        private final Image[][] piecesImages = new Image[2][7]; // 0: 红方, 1: 黑方
        private final int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH]; // 棋盘状态
        private Point selectedPiece = null; // 选中的棋子位置
        private boolean isRedTurn; // 当前下棋方
        private final boolean[][] validMoves; // 用于存储有效移动位置

        public ChessPanel(boolean isRed) {
            backgroundImage = new ImageIcon(projectPath + "qizi\\xqboard.gif").getImage();
            selectImage = new ImageIcon(projectPath + "qizi\\select.gif").getImage();
            loadPieceImages();
            initializeBoard(isRed);
            isRedTurn = true;
            validMoves = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleMouseClick(e);
                }
            });
        }

        private void loadPieceImages() {
            for (int i = 0; i < 7; i++) {
                piecesImages[0][i] = new ImageIcon(projectPath + "qizi\\" + (i + 1) + ".gif").getImage();
                piecesImages[1][i] = new ImageIcon(projectPath + "qizi\\" + (i + 8) + ".gif").getImage();
            }
        }

        private void initializeBoard(boolean isRed) {
            if (isRed) {
                // 红方在下方
                setupRedBottom();
            } else {
                // 黑方在下方
                setupBlackBottom();
            }
        }

        private void setupRedBottom() {
            // 红方在下方的棋盘初始化
            // 红方
            board[9][0] = 1; board[9][8] = 1; // 车
            board[9][1] = 2; board[9][7] = 2; // 马
            board[9][2] = 3; board[9][6] = 3; // 相
            board[9][3] = 4; board[9][5] = 4; // 士
            board[9][4] = 5; // 帅
            board[7][1] = 6; board[7][7] = 6; // 炮
            board[6][0] = 7; board[6][2] = 7; board[6][4] = 7; board[6][6] = 7; board[6][8] = 7; // 兵

            // 黑方
            board[0][0] = 8; board[0][8] = 8; // 车
            board[0][1] = 9; board[0][7] = 9; // 马
            board[0][2] = 10; board[0][6] = 10; // 象
            board[0][3] = 11; board[0][5] = 11; // 士
            board[0][4] = 12; // 将
            board[2][1] = 13; board[2][7] = 13; // 炮
            board[3][0] = 14; board[3][2] = 14; board[3][4] = 14; board[3][6] = 14; board[3][8] = 14; // 卒
        }

        private void setupBlackBottom() {
            // 黑方在下方的棋盘初始化
            // 黑方
            board[9][0] = 8; board[9][8] = 8; // 车
            board[9][1] = 9; board[9][7] = 9; // 马
            board[9][2] = 10; board[9][6] = 10; // 象
            board[9][3] = 11; board[9][5] = 11; // 士
            board[9][4] = 12; // 将
            board[7][1] = 13; board[7][7] = 13; // 炮
            board[6][0] = 14; board[6][2] = 14; board[6][4] = 14; board[6][6] = 14; board[6][8] = 14; // 卒

            // 红方
            board[0][0] = 1; board[0][8] = 1; // 车
            board[0][1] = 2; board[0][7] = 2; // 马
            board[0][2] = 3; board[0][6] = 3; // 相
            board[0][3] = 4; board[0][5] = 4; // 士
            board[0][4] = 5; // 帅
            board[2][1] = 6; board[2][7] = 6; // 炮
            board[3][0] = 7; board[3][2] = 7; board[3][4] = 7; board[3][6] = 7; board[3][8] = 7; // 兵
        }

        private void handleMouseClick(MouseEvent e) {
            int col = (e.getX() - (int) ((CELL_SIZE - (CELL_SIZE * 0.9)) * 2.4)) / CELL_SIZE;
            int row = (int) ((e.getY() - (CELL_SIZE - (CELL_SIZE * 0.9)) * 2) / CELL_SIZE);

            if (col >= 0 && col < BOARD_WIDTH && row >= 0 && row < BOARD_HEIGHT) {
                if (selectedPiece == null) {
                    // 选择棋子
                    if (board[row][col] != 0 && isCurrentPlayerPiece(row, col)) {
                        selectedPiece = new Point(col, row);
                        calculateValidMoves(selectedPiece);
                    }
                } else {
                    // 移动棋子
                    int piece = board[selectedPiece.y][selectedPiece.x];
                    boolean isRedPiece = piece >= 1 && piece <= 7;
                    if (ChessLogic.isValidMove(board, piece, selectedPiece.y, selectedPiece.x, row, col, isRed, isRedPiece, isRedTurn)) {
                        board[row][col] = piece;
                        board[selectedPiece.y][selectedPiece.x] = 0;
                        isRedTurn = !isRedTurn; // 切换下棋方

                        if (ChessLogic.isCheckmate(board, isRed, isRedTurn))
                            if (!isRed) JOptionPane.showMessageDialog(this, "你赢了");
                            else JOptionPane.showMessageDialog(this, "你寄了");

                    }
                    selectedPiece = null;
                    clearValidMoves();
                }
                repaint();
            }
        }

        private void calculateValidMoves(Point selectedPiece) {
            clearValidMoves();
            int piece = board[selectedPiece.y][selectedPiece.x];
            boolean isRedPiece = piece >= 1 && piece <= 7;
            for (int row = 0; row < BOARD_HEIGHT; row++) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    if (ChessLogic.isValidMove(board, piece, selectedPiece.y, selectedPiece.x, row, col, isRed, isRedPiece, isRedTurn)) {
                        validMoves[row][col] = true;
                    }
                }
            }
        }

        private void clearValidMoves() {
            for (int row = 0; row < BOARD_HEIGHT; row++) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    validMoves[row][col] = false;
                }
            }
        }

        private boolean isCurrentPlayerPiece(int row, int col) {
            int piece = board[row][col];
            if (isRedTurn) {
                return piece >= 1 && piece <= 7; // 红方棋子
            } else {
                return piece >= 8 && piece <= 14; // 黑方棋子
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            drawPieces(g);
            drawSelection(g);
            drawValidMoves(g);
        }

        private void drawPieces(Graphics g) {
            int pieceSize = (int) (CELL_SIZE * 0.9); // 缩小棋子大小
            int offsetX = (int) ((CELL_SIZE - pieceSize) * 2.4); // 水平中心对齐偏移
            int offsetY = (CELL_SIZE - pieceSize) * 2; // 垂直中心对齐偏移

            for (int row = 0; row < BOARD_HEIGHT; row++) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    int piece = board[row][col];
                    if (piece != 0) {
                        int player = (piece - 1) / 7; // 0: 红方, 1: 黑方
                        int pieceIndex = (piece - 1) % 7;
                        g.drawImage(piecesImages[player][pieceIndex], col * CELL_SIZE + offsetX, row * CELL_SIZE + offsetY, pieceSize, pieceSize, this);
                    }
                }
            }
        }

        private void drawSelection(Graphics g) {
            if (selectedPiece != null) {
                int pieceSize = (int) (CELL_SIZE * 0.9);
                int offsetX = (int) ((CELL_SIZE - pieceSize) * 2.4);
                int offsetY = (CELL_SIZE - pieceSize) * 2;
                g.drawImage(selectImage, selectedPiece.x * CELL_SIZE + offsetX, selectedPiece.y * CELL_SIZE + offsetY, pieceSize, pieceSize, this);
            }
        }

        private void drawValidMoves(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 启用抗锯齿

            for (int row = 0; row < BOARD_HEIGHT; row++) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    if (validMoves[row][col]) {
                        int x = col * CELL_SIZE + (int) ((CELL_SIZE - (CELL_SIZE * 0.9)) * 2.2);
                        int y = (int) (row * CELL_SIZE + (CELL_SIZE - (CELL_SIZE * 0.9)) * 1.6);

                        // 光点直径
                        int dotDiameter = CELL_SIZE / 4;
                        int offset = (CELL_SIZE - dotDiameter) / 2;

                        // 创建径向渐变
                        float radius = dotDiameter / 2f;
                        Point2D center = new Point2D.Float(x + offset + radius, y + offset + radius);
                        float[] dist = {0.0f, 1.0f};
                        Color[] colors = {new Color(255, 255, 0, 255), new Color(255, 255, 0, 0)};
                        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);

                        g2d.setPaint(p);
                        g2d.fillOval(x + offset, y + offset, dotDiameter, dotDiameter);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new BoardGUI(false);
    }
}
