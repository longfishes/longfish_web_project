package com.longfish.chess;

import java.util.Arrays;

public class ChessLogic {

    public static boolean movable(int[][] board, int piece, int fromRow, int fromCol, int toRow, int toCol, boolean isRedFirst, boolean isRedPiece) {
        // 检查索引是否在有效范围内
        if (fromRow < 0 || fromRow >= board.length || fromCol < 0 || fromCol >= board[0].length ||
                toRow < 0 || toRow >= board.length || toCol < 0 || toCol >= board[0].length) {
            return false;
        }

        // 检查目标位置是否是友方棋子
        int targetPiece = board[toRow][toCol];
        if ((isRedPiece && targetPiece >= 1 && targetPiece <= 7) || (!isRedPiece && targetPiece >= 8 && targetPiece <= 14)) {
            return false; // 不能吃友方棋子
        }

        // 检查是否是合法的棋子移动
        return switch (piece) {
            case 1, 8 -> isValidRookMove(board, fromRow, fromCol, toRow, toCol);
            case 2, 9 -> isValidKnightMove(board, fromRow, fromCol, toRow, toCol);
            case 3, 10 -> isValidBishopMove(board, fromRow, fromCol, toRow, toCol);
            case 4, 11 -> isValidAdvisorMove(fromRow, fromCol, toRow, toCol, piece < 8 == isRedFirst);
            case 5, 12 -> isValidKingMove(fromRow, fromCol, toRow, toCol, piece < 8 == isRedFirst);
            case 6, 13 -> isValidCannonMove(board, fromRow, fromCol, toRow, toCol);
            case 7, 14 -> isValidPawnMove(fromRow, fromCol, toRow, toCol, isRedFirst, isRedPiece);
            default -> false;
        };
    }

    public static boolean isValidMove(int[][] board, int piece, int fromRow, int fromCol, int toRow, int toCol, boolean isRedFirst, boolean isRedPiece, boolean isRedTurn) {
        // 检查索引是否在有效范围内
        if (fromRow < 0 || fromRow >= board.length || fromCol < 0 || fromCol >= board[0].length ||
            toRow < 0 || toRow >= board.length || toCol < 0 || toCol >= board[0].length) {
            return false;
        }

        // 检查目标位置是否是友方棋子
        int targetPiece = board[toRow][toCol];
        if ((isRedPiece && targetPiece >= 1 && targetPiece <= 7) || (!isRedPiece && targetPiece >= 8 && targetPiece <= 14)) {
            return false; // 不能吃友方棋子
        }

        // 检查是否是合法的棋子移动
        boolean isValid = switch (piece) {
            case 1, 8 -> isValidRookMove(board, fromRow, fromCol, toRow, toCol);
            case 2, 9 -> isValidKnightMove(board, fromRow, fromCol, toRow, toCol);
            case 3, 10 -> isValidBishopMove(board, fromRow, fromCol, toRow, toCol);
            case 4, 11 -> isValidAdvisorMove(fromRow, fromCol, toRow, toCol, piece < 8 == isRedFirst);
            case 5, 12 -> isValidKingMove(fromRow, fromCol, toRow, toCol, piece < 8 == isRedFirst);
            case 6, 13 -> isValidCannonMove(board, fromRow, fromCol, toRow, toCol);
            case 7, 14 -> isValidPawnMove(fromRow, fromCol, toRow, toCol, isRedFirst, isRedPiece);
            default -> false;
        };

        if (!isValid) return false;

        // 模拟移动棋子
        int[][] tempBoard = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
        tempBoard[toRow][toCol] = piece;
        tempBoard[fromRow][fromCol] = 0;

        // 检查移动后是否将军
        return !isInCheck(tempBoard, isRedFirst, isRedTurn);
    }

    public static boolean isInCheck(int[][] board, boolean isRed, boolean isRedTurn) {
        // 检查当前棋盘状态下是否将军
        int kingPiece = isRedTurn ? 5 : 12;
        int kingRow = -1, kingCol = -1;

        // 找到将/帅的位置
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == kingPiece) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
        }

        // 检查是否有敌方棋子可以攻击将/帅
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                int piece = board[row][col];
                if ((isRedTurn && piece >= 8 && piece <= 14) || (!isRedTurn && piece >= 1 && piece <= 7)) {
                    if (movable(board, piece, row, col, kingRow, kingCol, isRed, piece <= 7)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isCheckmate(int[][] board, boolean isRed, boolean isRedTurn) {
        // 检查是否绝杀
        for (int fromRow = 0; fromRow < board.length; fromRow++) {
            for (int fromCol = 0; fromCol < board[fromRow].length; fromCol++) {
                int piece = board[fromRow][fromCol];
                if ((isRedTurn && piece >= 1 && piece <= 7) || (!isRedTurn && piece >= 8 && piece <= 14)) {
                    for (int toRow = 0; toRow < board.length; toRow++) {
                        for (int toCol = 0; toCol < board[toRow].length; toCol++) {
                            if (isValidMove(board, piece, fromRow, fromCol, toRow, toCol, isRed, piece <= 7, isRedTurn)) {
                                return false; // 有合法移动，不是绝杀
                            }
                        }
                    }
                }
            }
        }
        return isInCheck(board, isRed, isRedTurn); // 无合法移动且被将军
    }

    private static boolean isValidRookMove(int[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        // 车的移动逻辑
        if (fromRow != toRow && fromCol != toCol) return false;
        if (fromRow == toRow) {
            int step = (toCol > fromCol) ? 1 : -1;
            for (int col = fromCol + step; col != toCol; col += step) {
                if (board[fromRow][col] != 0) return false;
            }
        } else {
            int step = (toRow > fromRow) ? 1 : -1;
            for (int row = fromRow + step; row != toRow; row += step) {
                if (board[row][fromCol] != 0) return false;
            }
        }
        return true;
    }

    private static boolean isValidKnightMove(int[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        // 马的移动逻辑
        int dRow = Math.abs(toRow - fromRow);
        int dCol = Math.abs(toCol - fromCol);
        if (dRow == 2 && dCol == 1) {
            return board[(fromRow + toRow) / 2][fromCol] == 0;
        } else if (dRow == 1 && dCol == 2) {
            return board[fromRow][(fromCol + toCol) / 2] == 0;
        }
        return false;
    }

    private static boolean isValidBishopMove(int[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        // 相/象的移动逻辑
        if (Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            if (board[midRow][midCol] != 0) return false;

            if (fromRow <= 4 && toRow > 4) return false; // 不能过河
            return fromRow <= 4 || toRow > 4;
        }
        return false;
    }

    private static boolean isValidAdvisorMove(int fromRow, int fromCol, int toRow, int toCol, boolean isDown) {
        // 士的移动逻辑
        if (Math.abs(toRow - fromRow) == 1 && Math.abs(toCol - fromCol) == 1) {
            if (isDown && (toRow < 7 || toCol < 3 || toCol > 5)) return false;
            return isDown || (toRow <= 2 && toCol >= 3 && toCol <= 5);
        }
        return false;
    }

    private static boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol, boolean isDown) {
        // 帅/将的移动逻辑
        if ((Math.abs(toRow - fromRow) == 1 && toCol == fromCol) || (Math.abs(toCol - fromCol) == 1 && toRow == fromRow)) {
            if (isDown && (toRow < 7 || toCol < 3 || toCol > 5)) return false;
            return isDown || (toRow <= 2 && toCol >= 3 && toCol <= 5);
        }
        return false;
    }

    private static boolean isValidCannonMove(int[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        // 炮的移动逻辑
        if (fromRow != toRow && fromCol != toCol) return false;
        int count = 0;
        if (fromRow == toRow) {
            int step = (toCol > fromCol) ? 1 : -1;
            for (int col = fromCol + step; col != toCol; col += step) {
                if (board[fromRow][col] != 0) count++;
            }
        } else {
            int step = (toRow > fromRow) ? 1 : -1;
            for (int row = fromRow + step; row != toRow; row += step) {
                if (board[row][fromCol] != 0) count++;
            }
        }
        return (count == 0 && board[toRow][toCol] == 0) || (count == 1 && board[toRow][toCol] != 0);
    }

    private static boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol, boolean isRed, boolean isRedPiece) {
        // 兵/卒的移动逻辑
        if (isRedPiece) {
            // 红兵的移动逻辑
            if (isRed) {
                // 红方在下方
                if (toRow > fromRow) return false; // 不能后退
            } else {
                // 红方在上方
                if (toRow < fromRow) return false; // 不能后退
            }
            if ((!isRed && fromRow < 5) || (isRed && fromRow > 4)) {
                if (fromRow == toRow) return false; // 过河前不能横走
            }
        } else {
            // 黑卒的移动逻辑
            if (isRed) {
                // 黑方在上方
                if (toRow < fromRow) return false; // 不能后退
            } else {
                // 黑方在下方
                if (toRow > fromRow) return false; // 不能后退
            }
            if ((isRed && fromRow < 5) || (!isRed && fromRow > 4)) {
                if (fromRow == toRow) return false; // 过河前不能横走
            }
        }
        return Math.abs(toRow - fromRow) + Math.abs(toCol - fromCol) == 1;
    }

}
