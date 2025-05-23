package com.checkers.cw_checkers;

import com.checkers.cw_checkers.view.MenuView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        MenuView menuView = new MenuView(stage);
        Scene scene = new Scene(menuView.getRoot(), 800, 720);
        stage.setTitle("Checkers Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
