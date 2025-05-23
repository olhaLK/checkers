package com.checkers.cw_checkers.view;

import com.checkers.cw_checkers.controller.GameController;
import com.checkers.cw_checkers.model.Board;
import com.checkers.cw_checkers.model.Piece;
import com.checkers.cw_checkers.model.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameView {
    private final BorderPane root = new BorderPane();
    private final GridPane boardGrid = new GridPane();
    private final Label turnLabel = new Label();
    private final Label scoreLabel = new Label();
    private final Label timerLabel = new Label();
    private final Button finishButton = new Button("Завершити гру");

    private final GameController controller;
    private final Board board;
    private int remainingTime;
    private Timeline countdown;

    private int selectedRow = -1;
    private int selectedCol = -1;

    public GameView(Stage stage, GameController controller) {
        this.controller = controller;
        this.board = controller.getBoard();

        HBox topPanel = new HBox(40);
        topPanel.setPadding(new Insets(10));
        topPanel.setAlignment(Pos.CENTER);

        turnLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        timerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        finishButton.setStyle("-fx-background-color: #E58C8A; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8px;");
        finishButton.setOnAction(e -> {
            if (!controller.isFinished()) {
                controller.forceFinishGame();
                showRestartDialog();
            }
        });

        topPanel.getChildren().addAll(turnLabel, scoreLabel, timerLabel, finishButton);

        boardGrid.setAlignment(Pos.CENTER);
        drawBoard();

        root.setTop(topPanel);
        root.setCenter(boardGrid);

        startTimer();
        updateUI();
    }

    private void drawBoard() {
        boardGrid.getChildren().clear();

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                StackPane cell = new StackPane();
                Rectangle tile = new Rectangle(80, 80);
                tile.setFill((row + col) % 2 == 0 ? Color.web("#FCF4DA") : Color.web("#A08B77"));

                final int finalRow = row;
                final int finalCol = col;
                tile.setOnMouseClicked(e -> handleCellClick(finalRow, finalCol));
                cell.getChildren().add(tile);

                Piece piece = board.getPiece(row, col);
                if (piece != null) {
                    String name = piece.getColor() == Piece.Color.GREEN ? "piece_green" : "piece_pink";
                    if (piece.getType() == Piece.Type.QUEEN) {
                        name = name.replace("piece", "queen");
                    }
                    String path = "/com/checkers/cw_checkers/" + name + ".png";
                    var stream = GameView.class.getResourceAsStream(path);
                    if (stream != null) {
                        Image img = new Image(stream);
                        ImageView iv = new ImageView(img);
                        iv.setFitWidth(70);
                        iv.setFitHeight(70);
                        iv.setMouseTransparent(true);
                        cell.getChildren().add(iv);
                    }
                }

                boardGrid.add(cell, col, row);
            }
        }
    }

    private void handleCellClick(int row, int col) {
        if (selectedRow == -1 && selectedCol == -1) {
            Piece piece = board.getPiece(row, col);
            if (piece != null && piece.getColor() == controller.getCurrentPlayer().getColor()) {
                selectedRow = row;
                selectedCol = col;
            }
        } else {
            boolean moved = controller.tryMove(selectedRow, selectedCol, row, col);
            drawBoard(); // 🔁 Оновлюється навіть при помилці
            updateUI();
            if (moved && controller.isGameOver()) {
                controller.stopGame("Переможець: " + controller.getWinnerName());
                showRestartDialog();
            }
            selectedRow = -1;
            selectedCol = -1;
        }
    }



    private void updateUI() {
        Player p1 = controller.getPlayer1();
        Player p2 = controller.getPlayer2();
        turnLabel.setText("Хід: " + controller.getCurrentPlayer().getName());
        scoreLabel.setText(
                controller.getPlayer1().getName() + " (і): " + controller.getPlayer1().getKills() + " | " +
                        controller.getPlayer2().getName() + " (і): " + controller.getPlayer2().getKills()
        );
    }

    private void startTimer() {
        remainingTime = controller.getTimeSeconds();
        if (remainingTime <= 0) {
            timerLabel.setText("Без обмеження часу");
            return;
        }

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remainingTime--;
            timerLabel.setText("Час: " + formatTime(remainingTime));
            if (remainingTime <= 0) {
                controller.stopGame("Час вичерпано. Переміг: " + controller.getWinnerName());
                showRestartDialog();
            }
        }));
        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();
    }

    private String formatTime(int sec) {
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    private void showRestartDialog() {
        ButtonType restart = new ButtonType("Грати знову");
        ButtonType exit = new ButtonType("Вийти", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                controller.getLastMessage(),
                restart, exit);
        alert.setTitle("Кінець гри");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(type -> {
            if (type == restart) {
                MenuView menu = new MenuView((Stage) root.getScene().getWindow());
                root.getScene().setRoot(menu.getRoot());
            } else {
                Platform.exit();
            }
        });
    }

    public BorderPane getRoot() {
        return root;
    }
}
