package com.checkers.cw_checkers.view;

import com.checkers.cw_checkers.controller.GameController;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuView {
    private final VBox root = new VBox(15);

    public MenuView(Stage stage) {
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #FCF4DA;");

        Label title = new Label("CHECKERS");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #A08B77;");

        TextField player1Field = new TextField();
        player1Field.setPromptText("Ім’я гравця 1");
        player1Field.setStyle("-fx-font-size: 16px;");

        TextField player2Field = new TextField();
        player2Field.setPromptText("Ім’я гравця 2 або 'Bot'");
        player2Field.setStyle("-fx-font-size: 16px;");

        ComboBox<String> modeBox = new ComboBox<>();
        modeBox.getItems().addAll("2 гравці", "Гравець vs Бот");
        modeBox.setValue("2 гравці");

        ComboBox<String> timerBox = new ComboBox<>();
        timerBox.getItems().addAll("Без обмежень", "5 хв", "10 хв");
        timerBox.setValue("Без обмежень");

        Button startButton = new Button("ПОЧАТИ ГРУ");
        startButton.setStyle("-fx-background-color: #9CE1AE; -fx-font-size: 18px; -fx-text-fill: white; -fx-background-radius: 12px; -fx-padding: 10px 20px;");

        // ✅ ОНОВЛЕНА умова активації кнопки:
        startButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            boolean isBot = modeBox.getValue().equals("Гравець vs Бот");
            return player1Field.getText().trim().isEmpty() ||
                    (!isBot && player2Field.getText().trim().isEmpty());
        }, player1Field.textProperty(), player2Field.textProperty(), modeBox.valueProperty()));

        // 🔁 Логіка перемикання режиму
        modeBox.setOnAction(e -> {
            if (modeBox.getValue().equals("Гравець vs Бот")) {
                player2Field.setText("Bot");
                player2Field.setDisable(true);
            } else {
                player2Field.setText("");
                player2Field.setDisable(false);
            }
        });

        // ▶ Запуск гри
        startButton.setOnAction(e -> {
            String name1 = player1Field.getText().trim();
            String name2 = player2Field.getText().trim();
            String timer = timerBox.getValue();

            GameController controller = new GameController(name1, name2, timer);
            GameView gameView = new GameView(stage, controller);
            stage.setScene(new Scene(gameView.getRoot(), 800, 720));
        });

        root.getChildren().addAll(title, player1Field, player2Field, modeBox, timerBox, startButton);
    }

    public VBox getRoot() {
        return root;
    }
}
