package com.longfish.logic;

public class Logic {

    // 静态方法：判断棋盘上是否有五子连珠的玩家获胜，返回null/true/false
    public static Boolean checkWin(Boolean[][] board) {
        int rows = board.length;       // 获取棋盘的行数
        int cols = board[0].length;    // 获取棋盘的列数

        // 遍历棋盘上的每一个位置
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                if (board[x][y] != null) {  // 如果当前位置有棋子
                    boolean isBlack = board[x][y];
                    // 检查四个方向：水平、垂直、左斜、右斜
                    if (checkDirection(board, x, y, 1, 0, isBlack)) {  // 水平
                        return isBlack;
                    }
                    if (checkDirection(board, x, y, 0, 1, isBlack)) {  // 垂直
                        return isBlack;
                    }
                    if (checkDirection(board, x, y, 1, 1, isBlack)) {  // 左斜
                        return isBlack;
                    }
                    if (checkDirection(board, x, y, 1, -1, isBlack)) {  // 右斜
                        return isBlack;
                    }
                }
            }
        }
        return null;  // 如果没有任何一方获胜
    }

    // 静态方法：检查某一方向上的连续五子
    private static boolean checkDirection(Boolean[][] board, int x, int y, int dx, int dy, boolean isBlack) {
        int count = 1;  // 当前棋子算作一个，初始为1
        int rows = board.length;
        int cols = board[0].length;

        // 向一个方向检查
        for (int i = 1; i < 5; i++) {
            int newX = x + dx * i;
            int newY = y + dy * i;
            // 检查是否越界或者棋子颜色不匹配
            if (newX < 0 || newX >= rows || newY < 0 || newY >= cols || board[newX][newY] == null || board[newX][newY] != isBlack) {
                break;
            }
            count++;
        }

        // 向反方向检查
        for (int i = 1; i < 5; i++) {
            int newX = x - dx * i;
            int newY = y - dy * i;
            // 检查是否越界或者棋子颜色不匹配
            if (newX < 0 || newX >= rows || newY < 0 || newY >= cols || board[newX][newY] == null || board[newX][newY] != isBlack) {
                break;
            }
            count++;
        }

        // 如果连成了五个子，返回 true
        return count >= 5;
    }

}
