package com.checkers.cw_checkers.model;

public class Board {
    public static final int SIZE = 8;
    private final Piece[][] grid;

    public Board() {
        grid = new Piece[SIZE][SIZE];
        initBoard();
    }

    private void initBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = (row % 2); col < SIZE; col += 2) {
                if (row < 3) {
                    grid[row][col] = new Piece(Piece.Color.PINK);
                } else if (row > 4) {
                    grid[row][col] = new Piece(Piece.Color.GREEN);
                }
            }
        }
    }

    public Piece getPiece(int row, int col) {
        return grid[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        grid[row][col] = piece;
    }

    public boolean hasPieces(Piece.Color color) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Piece piece = grid[r][c];
                if (piece != null && piece.getColor() == color) return true;
            }
        }
        return false;
    }


    public void removePiece(int row, int col) {
        Piece removed = grid[row][col];
        if (removed != null) {
            if (removed.getColor() == Piece.Color.GREEN)
                Player.decreaseScore(Piece.Color.GREEN);
            else if (removed.getColor() == Piece.Color.PINK)
                Player.decreaseScore(Piece.Color.PINK);
        }
        grid[row][col] = null;
    }


    public Piece[][] getGrid() {
        return grid;
    }
}
