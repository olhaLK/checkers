package com.checkers.cw_checkers.controller;

import com.checkers.cw_checkers.model.Board;
import com.checkers.cw_checkers.model.Piece;
import com.checkers.cw_checkers.model.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class GameController {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private Timeline timer;
    private int timeSeconds;
    private String lastMessage = "";
    private boolean isGameFinished = false;

    public GameController(String name1, String name2, String timerMode) {
        this.board = new Board();
        this.player1 = new Player(name1, Piece.Color.GREEN);
        this.player2 = new Player(name2, Piece.Color.PINK);
        this.currentPlayer = player1;

        switch (timerMode) {
            case "5 хв" -> this.timeSeconds = 300;
            case "10 хв" -> this.timeSeconds = 600;
            default -> this.timeSeconds = -1;
        }

        if (timeSeconds > 0) {
            timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                timeSeconds--;
                if (timeSeconds <= 0) {
                    stopGame("Час вичерпано! Переміг: " + getOpponent().getName());
                }
            }));
            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();
        }
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public boolean tryMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (isGameFinished) return false;

        Piece moving = board.getPiece(fromRow, fromCol);
        if (moving == null || moving.getColor() != currentPlayer.getColor()) return false;

        int dRow = toRow - fromRow;
        int dCol = toCol - fromCol;

        if (moving.isQueen()) {
            if (Math.abs(dRow) != Math.abs(dCol)) return false;

            int stepRow = Integer.signum(dRow);
            int stepCol = Integer.signum(dCol);
            int r = fromRow + stepRow, c = fromCol + stepCol;

            boolean captured = false;
            int capturedRow = -1, capturedCol = -1;

            while (r != toRow && c != toCol) {
                Piece piece = board.getPiece(r, c);
                if (piece != null) {
                    if (piece.getColor() == moving.getColor()) return false;
                    if (captured) return false; // більше ніж 1 фігура — заборона
                    captured = true;
                    capturedRow = r;
                    capturedCol = c;
                }
                r += stepRow;
                c += stepCol;
            }

            if (board.getPiece(toRow, toCol) != null) return false;

            board.setPiece(toRow, toCol, moving);
            board.removePiece(fromRow, fromCol);

            if (captured) {
                board.removePiece(capturedRow, capturedCol);
                currentPlayer.incrementKills();
            }

            switchTurn();
            return true;
        }

        // Побиття звичайною фігурою
        if (Math.abs(dRow) == 2 && Math.abs(dCol) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            Piece middle = board.getPiece(midRow, midCol);

            if (middle != null && middle.getColor() != moving.getColor() && board.getPiece(toRow, toCol) == null) {
                board.setPiece(toRow, toCol, moving);
                board.removePiece(fromRow, fromCol);
                board.removePiece(midRow, midCol);
                checkPromotion(toRow, toCol);
                currentPlayer.incrementKills();
                switchTurn();
                return true;
            }
        }

        // Простий хід
        if (Math.abs(dRow) == 1 && Math.abs(dCol) == 1 && board.getPiece(toRow, toCol) == null) {
            board.setPiece(toRow, toCol, moving);
            board.removePiece(fromRow, fromCol);
            checkPromotion(toRow, toCol);
            switchTurn();
            return true;
        }

        return false;
    }



    private void checkPromotion(int row, int col) {
        Piece piece = board.getPiece(row, col);
        if (piece != null) {
            if ((piece.getColor() == Piece.Color.GREEN && row == 0) ||
                    (piece.getColor() == Piece.Color.PINK && row == Board.SIZE - 1)) {
                piece.promote();
            }
        }
    }

    public boolean isGameOver() {
        return !board.hasPieces(Piece.Color.GREEN) || !board.hasPieces(Piece.Color.PINK);

    }

    public String getWinnerName() {
        if (!isGameOver()) return "Гра триває";
        if (player1.getScore() > player2.getScore()) return player1.getName();
        if (player2.getScore() > player1.getScore()) return player2.getName();
        return "Нічия";
    }

    public void stopGame(String message) {
        this.lastMessage = message;
        isGameFinished = true;
        if (timer != null) timer.stop();
    }

    public void forceFinishGame() {
        stopGame("Гру завершено достроково. Підсумок: " + getWinnerName());
    }

    public boolean isFinished() {
        return isGameFinished;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    private Player getOpponent() {
        return currentPlayer == player1 ? player2 : player1;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }
}
