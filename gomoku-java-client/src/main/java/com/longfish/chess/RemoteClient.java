package com.longfish.chess;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Stack;

public class RemoteClient extends JFrame {

    private static final int BOARD_WIDTH = 9;  // 棋盘宽度（列数）
    private static final int BOARD_HEIGHT = 10; // 棋盘高度（行数）
    private static final int CELL_SIZE = 60;    // 每个格子的大小
    private static final int BOARD_SIZE_X = BOARD_WIDTH * CELL_SIZE; // 棋盘总宽度
    private static final int BOARD_SIZE_Y = BOARD_HEIGHT * CELL_SIZE; // 棋盘总高度
    private static final String projectPath = "gomoku-java-client\\src\\main\\resources\\";

    private final Stack<Move> moveHistory = new Stack<>();
    private final ChessPanel panel;
    private final boolean isRed;
    private boolean available;

    private final String SERVER_ADDRESS;
    private final int SERVER_PORT;
    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;

    private final JButton drawButton;
    private final JButton undoButton;
    private final JButton surrenderButton;
    private final JButton startButton;

    private JLabel myIconLabel;
    private JLabel opponentIconLabel;
    private final ImageIcon transparentIcon = new ImageIcon(new BufferedImage(60, 59, BufferedImage.TYPE_INT_ARGB));

    private final String myName;
    private final String myFace;
    private String oppoName;
    private String oppoFace;
    private JTextArea chatArea;

    private PrintWriter chatWriter;
    private BufferedReader chatReader;

    private boolean isOpen = false;
    private boolean isRunaway = false;
    private boolean isDraw = false;
    private boolean isGameOver = false;

    public RemoteClient(boolean isRed, String address, int port, int roomId, String myName, String myFace) {
        this.isRed = isRed;
        this.SERVER_ADDRESS = address;
        this.SERVER_PORT = port;
        this.available = false;
        this.myName = myName;
        this.myFace = myFace;
        start();

        setTitle("中国象棋对战房间窗口");
        setSize(BOARD_SIZE_X + 440, BOARD_SIZE_Y + 213); // 调整窗口大小
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                // 仅在用户点击关闭按钮时触发
                if (windowEvent.getID() == WindowEvent.WINDOW_CLOSING) {
                    onExitButtonClick();
                }
            }
        });
        setLocationRelativeTo(null);
        setResizable(false);

        // 设置布局
        setLayout(new BorderLayout());

        // 中央面板，包含棋盘和按钮
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        // 北侧面板，展示房间信息和退出按钮
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // 房间信息和退出按钮面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(BOARD_SIZE_X, 26)); // 设置固定高度
        JLabel roomInfoLabel = new JLabel("<<< 象棋游戏---房间 " + roomId + " >>>", SwingConstants.LEFT); // 初始化成员变量
        JButton exitButton = new JButton("退出");
        infoPanel.add(roomInfoLabel, BorderLayout.CENTER);
        infoPanel.add(exitButton, BorderLayout.EAST);
        topPanel.add(infoPanel, BorderLayout.NORTH); // 添加到topPanel的北侧

        // 蓝色间隙面板
        JPanel blueSpacerPanel = new JPanel();
        blueSpacerPanel.setBackground(new Color(81, 113, 158));
        blueSpacerPanel.setPreferredSize(new Dimension(BOARD_SIZE_X, 60)); // 设置固定高度
        topPanel.add(blueSpacerPanel, BorderLayout.SOUTH); // 添加到topPanel的南侧

        centerPanel.add(topPanel, BorderLayout.NORTH); // 添加到centerPanel的北侧

        // 棋盘面板
        panel = new ChessPanel(isRed);
        centerPanel.add(panel, BorderLayout.CENTER);

        // 下侧面板
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(81, 113, 158));
        bottomPanel.setPreferredSize(new Dimension(BOARD_SIZE_X, 66)); // 设置固定高度

        startButton = new JButton("开始");
        startButton.setEnabled(isRed);
        startButton.addActionListener(e -> onStartButtonClick());
        bottomPanel.add(startButton);

        drawButton = new JButton("求和");
        drawButton.setEnabled(false);
        drawButton.addActionListener(e -> onDrawButtonClick());
        bottomPanel.add(drawButton);

        undoButton = new JButton("悔棋");
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> onUndoButtonClick());
        bottomPanel.add(undoButton);

        surrenderButton = new JButton("认输");
        surrenderButton.setEnabled(false);
        surrenderButton.addActionListener(e -> onSurrenderButtonClick());
        bottomPanel.add(surrenderButton);

        exitButton.addActionListener(e -> onExitButtonClick());
        infoPanel.add(exitButton, BorderLayout.EAST);

        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // 左侧面板
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(200, BOARD_SIZE_Y));
        leftPanel.setBackground(Color.WHITE);

        JPanel leftTopPanel = new JPanel();
        leftTopPanel.setBackground(Color.WHITE);
        leftTopPanel.add(createUserInfoPanel("对手",  oppoFace + ".gif", oppoName, false));

        JPanel leftBottomPanel = new JPanel();
        leftBottomPanel.setBackground(Color.WHITE);
        leftBottomPanel.add(createUserInfoPanel("自己", myFace + ".gif", myName, true));

        leftPanel.add(leftTopPanel);
        leftPanel.add(leftBottomPanel);
        add(leftPanel, BorderLayout.WEST);

        // 右侧面板
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(200, BOARD_SIZE_Y));
        rightPanel.setBackground(Color.WHITE);

        JPanel rightTopPanel = new JPanel();
        rightTopPanel.setBackground(Color.WHITE);
        rightTopPanel.add(createUserListPanel());

        JPanel rightBottomPanel = new JPanel();
        rightBottomPanel.setBackground(Color.WHITE);
        rightBottomPanel.add(createChatPanel());

        rightPanel.add(rightTopPanel);
        rightPanel.add(rightBottomPanel);
        add(rightPanel, BorderLayout.EAST);

        if (connectToChatServer(port + 100)) {
            startChatReceiver();
            setVisible(true);
            isOpen = true;
            for (Window window : Window.getWindows()) {
                if (window != this) window.dispose();
            }
        }
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
            if (!available) return;

            int col = (e.getX() - (int) ((CELL_SIZE - (CELL_SIZE * 0.9)) * 2.4)) / CELL_SIZE;
            int row = (int) ((e.getY() - (CELL_SIZE - (CELL_SIZE * 0.9)) * 2) / CELL_SIZE);

            if (col >= 0 && col < BOARD_WIDTH && row >= 0 && row < BOARD_HEIGHT) {
                if (selectedPiece == null) {
                    // 选择棋子
                    if (board[row][col] != 0 && isCurrentPlayerPiece(row, col)) {
                        selectedPiece = new Point(col, row);
                        calculateValidMoves(selectedPiece);
                        playSound("select.wav");
                    }
                } else {
                    // 移动棋子
                    int piece = board[selectedPiece.y][selectedPiece.x];
                    boolean isRedPiece = piece >= 1 && piece <= 7;
                    if (ChessLogic.isValidMove(board, piece, selectedPiece.y, selectedPiece.x, row, col, isRed, isRedPiece, isRedTurn)) {
                        sendNumbers(9 - selectedPiece.y, 8 - selectedPiece.x, 9 - row, 8 - col);
                        movePiece(selectedPiece.y, selectedPiece.x, row, col);
                        checkWin();
                    }
                    selectedPiece = null;
                    clearValidMoves();
                }
                repaint();
            }
        }

        private void movePiece(int fromRow, int fromCol, int row, int col) {
            boolean eatFlag = board[row][col] != 0;

            // 将当前移动存储到栈中
            moveHistory.push(new Move(fromRow, fromCol, row, col, board[row][col]));
            board[row][col] = board[fromRow][fromCol];
            board[fromRow][fromCol] = 0;
            isRedTurn = !isRedTurn;
            toggleIconVisibility();
            available = !available;

            // 播放音效
            if (ChessLogic.isInCheck(board, isRed, isRedTurn)) playSound("jiang.wav");
            else playSound(eatFlag ? "eat.wav" : "go.wav");

            selectedPiece = null;
            clearValidMoves();
            repaint();
            updateButtonStates();
        }

        private void playSound(String soundFileName) {
            try {
                File soundFile = new File(projectPath + "audio\\" + soundFileName);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 添加悔棋功能
        private void undoLastMove() {
            if (!moveHistory.isEmpty()) {
                for (int i = 0; i < 2; i++) {
                    Move lastMove = moveHistory.pop();
                    board[lastMove.fromRow][lastMove.fromCol] = board[lastMove.toRow][lastMove.toCol];
                    board[lastMove.toRow][lastMove.toCol] = lastMove.capturedPiece;
                }
                clearValidMoves();
                selectedPiece = null;
                repaint();
                updateButtonStates();
            }
        }

        private void checkWin() {
            if (ChessLogic.isCheckmate(board, isRed, isRedTurn)) {
                if (isRed == !isRedTurn) JOptionPane.showMessageDialog(this, myName + " 将杀获胜");
                else JOptionPane.showMessageDialog(this, oppoName + " 将杀获胜");
                isGameOver = true;
                exit();
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

    // 定义一个类来存储每一步的移动信息
    private static class Move {
        int fromRow, fromCol, toRow, toCol, capturedPiece;

        Move(int fromRow, int fromCol, int toRow, int toCol, int capturedPiece) {
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.toRow = toRow;
            this.toCol = toCol;
            this.capturedPiece = capturedPiece;
        }
    }

    public void start() {
        try {
            connectToServer();
            // 创建输入输出流
            reader = new DataInputStream(socket.getInputStream());
            writer = new DataOutputStream(socket.getOutputStream());

            if (isRed) {
                reader.readBoolean(); // 阻塞进程等待第二个玩家连接
                writer.writeUTF(myName);
                writer.writeUTF(myFace);
                this.oppoName = reader.readUTF();
                this.oppoFace = reader.readUTF();
            } else {
                this.oppoName = reader.readUTF();
                this.oppoFace = reader.readUTF();
                writer.writeUTF(myName);
                writer.writeUTF(myFace);
            }

            // 创建一个线程用于接收消息
            Thread receiverThread = new Thread(() -> {
                try {
                    while (true) {
                        // 等待接收两个整数
                        int num1 = reader.readInt();
                        int num2 = reader.readInt();
                        int num3 = reader.readInt();
                        int num4 = reader.readInt();

                        if (num1 == -1) {
                            if (num2 == -1) {
                                if (num3 == -1 && num4 == -1) {
                                    if (!isRed) sendNumbers(-1, -1, -1, -1);
                                    JOptionPane.showMessageDialog(this, "游戏开始！");
                                }
                            }
                            else if (num2 == -2) { // 求和
                                if (num3 == -2 && num4 == -2) {
                                    // 发来求和
                                    int result = JOptionPane.showConfirmDialog(this, "对方求和，是否同意？");
                                    if (result == JOptionPane.YES_OPTION) {
                                        isDraw = true;
                                        sendNumbers(-1, -2, -1, -2);
                                        exit();
                                    } else {
                                        isDraw = false;
                                        sendNumbers(-1, -2, -2, -1);
                                    }
                                } else if (num3 == -1 && num4 == -2) {
                                    JOptionPane.showMessageDialog(this, "对方同意求和！");
                                    isDraw = true;
                                    exit();
                                } else if (num3 == -2 && num4 == -1) {
                                    JOptionPane.showMessageDialog(this, "对方已拒绝");
                                    isDraw = false;
                                }
                            } else if (num2 == -3) { // 悔棋
                                if (num3 == -3 && num4 == -3) {
                                    // 发来悔棋
                                    int result = JOptionPane.showConfirmDialog(this, "对方发来悔棋，是否同意？");
                                    if (result == JOptionPane.YES_OPTION) {
                                        sendNumbers(-1, -3, -1, -3);
                                        panel.undoLastMove();
                                    } else {
                                        sendNumbers(-1, -3, -3, -1);
                                    }

                                } else if (num3 == -1 && num4 == -3) {
                                    // 同意悔棋
                                    panel.undoLastMove();
                                } else if (num3 == -3 && num4 == -1) {
                                    JOptionPane.showMessageDialog(this, "对方已拒绝");
                                }
                            } else if (num2 == -4) { // 认输
                                if (num3 == -4 && num4 == -4) {
                                    isRunaway = true;
                                    JOptionPane.showMessageDialog(this, "对方已认输！");
                                    exit();
                                }
                            }
                        } else {
                            panel.movePiece(num1, num2, num3, num4);
                            panel.checkWin();
                        }
                    }
                } catch (IOException e) {
                    if (!isRunaway && !isDraw && !isGameOver) {
                        JOptionPane.showMessageDialog(this, "对方已逃跑！");
                        exit();
                    }
                }
            });
            receiverThread.start();  // 启动接收线程

        } catch (IOException e) {
            exit();
        }
    }

    // 连接到服务器并初始化 socket
    private void connectToServer() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
    }

    // 发送两个整数到服务器
    public void sendNumbers(int num1, int num2, int num3, int num4) {
        try {
            if (writer != null) {
                writer.writeInt(num1);
                writer.writeInt(num2);
                writer.writeInt(num3);
                writer.writeInt(num4);
            } else {
                exit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JLabel createMenuLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true); // 使背景颜色可见
        label.setBackground(new Color(240, 240, 240)); // 设置背景颜色
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 设置边框
        label.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        label.setPreferredSize(new Dimension(180, 30)); // 设置首选大小
        return label;
    }

    // 封装的按钮点击事件方法
    private void onStartButtonClick() {
        startButton.setEnabled(false);
        updateButtonStates();
        available = true;
        updateButtonStates();
        sendNumbers(-1, -1, -1, -1);
    }

    private void onDrawButtonClick() {
        int result = JOptionPane.showConfirmDialog(this, "是否发起求和？");
        if (result == JOptionPane.YES_OPTION) {
            isDraw = true;
            sendNumbers(-1, -2, -2, -2);
        }
    }

    private void onUndoButtonClick() {
        if (moveHistory.size() < 2) {
            JOptionPane.showMessageDialog(this, "已是第一步！");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "是否发起悔棋？");
        if (result == JOptionPane.YES_OPTION) {
            sendNumbers(-1, -3, -3, -3);
        }
    }

    private void onSurrenderButtonClick() {
        int result = JOptionPane.showConfirmDialog(this, "是否确认认输？");
        if (result == JOptionPane.YES_OPTION) {
            isRunaway = true;
            sendNumbers(-1, -4, -4, -4);
            exit();
        }
    }

    private void onExitButtonClick() {
        int result = JOptionPane.showConfirmDialog(this, "逃跑会自动认输！是否确认退出？");
        if (result == JOptionPane.YES_OPTION) {
            isRunaway = true;
            sendNumbers(-1, -4, -4, -4);
            exit();
        }
    }

    private void updateButtonStates() {
        drawButton.setEnabled(available);
        undoButton.setEnabled(available);
        surrenderButton.setEnabled(available);
    }

    private JPanel createUserInfoPanel(String title, String avatarName, String userName, boolean isMyPanel) {
        ImageIcon optionalIcon = new ImageIcon(projectPath + "img\\r5.gif");
        ImageIcon avatar = new ImageIcon(projectPath + "face\\" + avatarName);
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BorderLayout());

        // 使用 createMenuLabel 创建标题
        JLabel titleLabel = createMenuLabel(title);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        userInfoPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建头像和名称的面板
        JPanel userDetailPanel = new JPanel();
        userDetailPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        userDetailPanel.setBackground(Color.WHITE);
        userDetailPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 25, 0));

        JLabel avatarLabel = new JLabel(avatar);
        userDetailPanel.add(avatarLabel);

        JLabel nameLabel = new JLabel(userName);
        userDetailPanel.add(nameLabel);

        userInfoPanel.add(userDetailPanel, BorderLayout.CENTER);

        // 创建落子方提示
        JLabel iconLabel = new JLabel(optionalIcon);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(Color.WHITE);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        userInfoPanel.add(iconLabel, BorderLayout.SOUTH);

        // 根据是己方还是对方，设置不同的图标标签
        if (isMyPanel) {
            myIconLabel = iconLabel;
        } else {
            opponentIconLabel = iconLabel;
        }
        if (isRed != isMyPanel) iconLabel.setIcon(transparentIcon); // 设置为透明图标

        return userInfoPanel;
    }

    private void toggleIconVisibility() {
        ImageIcon originIcon = new ImageIcon(projectPath + "img\\r5.gif");
        if (opponentIconLabel.getIcon().equals(transparentIcon)) {
            myIconLabel.setIcon(transparentIcon);
            opponentIconLabel.setIcon(originIcon);
        } else {
            myIconLabel.setIcon(originIcon);
            opponentIconLabel.setIcon(transparentIcon);
        }

    }

    private JPanel createUserListPanel() {
        String[] userNames;
        if (!isRed) userNames = new String[]{oppoName, myName};
        else userNames = new String[]{myName, oppoName};
        JPanel userListPanel = new JPanel();
        userListPanel.setLayout(new BorderLayout());
        userListPanel.setBackground(Color.WHITE); // 设置背景为白色

        // 创建标题
        JLabel titleLabel = createMenuLabel("用户列表");
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY), // 黑色边框
            BorderFactory.createEmptyBorder(5, 0, 5, 0) // 添加上下间距
        ));
        userListPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建用户列表面板
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        // 为每个用户名创建一个标签并添加到列表面板中
        for (String userName : userNames) {
            JLabel userLabel = new JLabel(userName);
            userLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐
            userLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // 添加上下间距
            listPanel.add(userLabel);
        }

        userListPanel.add(listPanel, BorderLayout.CENTER);

        // 添加空白面板用于占用空间
        JPanel spacerPanel = new JPanel();
        spacerPanel.setBackground(Color.WHITE); // 设置背景为白色
        spacerPanel.setPreferredSize(new Dimension(100, 290)); // 设置空白面板的大小
        userListPanel.add(spacerPanel, BorderLayout.SOUTH);

        return userListPanel;
    }

    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Color.WHITE); // 设置背景为白色

        // 创建标题
        JLabel titleLabel = createMenuLabel("聊天");
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY), // 黑色边框
            BorderFactory.createEmptyBorder(5, 0, 5, 0) // 添加上下间距
        ));
        chatPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建聊天框
        chatArea = new JTextArea();
        chatArea.setEditable(false); // 设置为不可编辑
        chatArea.setLineWrap(true); // 自动换行
        chatArea.setWrapStyleWord(true); // 以单词为单位换行
        chatArea.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 添加边框

        // 将聊天框放入滚动面板中
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setPreferredSize(new Dimension(100, 300));

        // 创建一个中间面板来设置背景和间距
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE); // 设置背景为白色
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // 添加顶部间距
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        chatPanel.add(centerPanel, BorderLayout.CENTER);

        // 创建输入框和发送按钮
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE); // 设置背景为白色
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // 添加顶部间距

        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("发送");

        // 添加回车键监听器
        inputField.addActionListener(e -> sendButton.doClick());

        sendButton.addActionListener(l -> {
            String input = inputField.getText();
            if (!input.equals("")) {
                String formattedMessage = myName + "：" + input;
                addTextToChat(formattedMessage);
                sendChatMessage(formattedMessage);
                inputField.setText("");
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        return chatPanel;
    }

    private void addTextToChat(String text) {
        if (chatArea != null) {
            chatArea.append(text + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); // 滚动到最新消息
        }
    }

    private boolean connectToChatServer(int chatPort) {
        try {
            Socket chatSocket = new Socket(SERVER_ADDRESS, chatPort);
            chatWriter = new PrintWriter(chatSocket.getOutputStream(), true);
            chatReader = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
        } catch (IOException e) {
//            JOptionPane.showMessageDialog(this, "无法连接到聊天服务器！");
            return false;
        }
        return true;
    }

    private void startChatReceiver() {
        Thread chatReceiverThread = new Thread(() -> {
            try {
                String message;
                while ((message = chatReader.readLine()) != null) {
                    addTextToChat(message);
                }
            } catch (Exception ignored) {}
        });
        chatReceiverThread.start();
    }

    private void sendChatMessage(String message) {
        if (chatWriter != null) {
            chatWriter.println(message);
        } else {
            JOptionPane.showMessageDialog(this, "无法发送消息！");
        }
    }

    private void exit() {
        // 重置房间
        int roomId = SERVER_PORT - 11111;
        try (Socket socket = new Socket(SERVER_ADDRESS, 11111)) {
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

        try {
            socket.close();
        } catch (Exception ignored) {}

        if (isOpen) {
            for (Window window : Window.getWindows()) {
                window.dispose();
            }
            new Thread(() -> new MenuClient(SERVER_ADDRESS, myName, myFace, true)).start();
        }
    }

}
