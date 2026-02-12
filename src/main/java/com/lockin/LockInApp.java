package com.lockin;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LockInApp extends Application {

    private GameModel gameModel = new GameModel();
    private CardView cardView;
    private Label scoreLabel = new Label();
    private Label powerUpLabel = new Label("Roll the Die to Start");

    @Override
    public void start(Stage primaryStage) {
        cardView = new CardView();

        // --- UI Setup ---
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        // Force a color so we know if the UI is rendering
        root.setStyle("-fx-background-color: #0f0f1b;");

        scoreLabel.setText(gameModel.getScoreString());
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Arial';");

        powerUpLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-size: 18px; -fx-font-style: italic;");

        // --- Buttons ---
        Button btnA = new Button("Team A Point");
        Button btnB = new Button("Team B Point");
        Button btnDraw = new Button("Draw Card");

        styleButton(btnA, "#4ecca3");
        styleButton(btnB, "#4ecca3");
        styleButton(btnDraw, "#d4af37");

        // --- Event Wiring ---
        btnDraw.setOnAction(e -> handleDraw());
        btnA.setOnAction(e -> {
            gameModel.addScore(true);
            scoreLabel.setText(gameModel.getScoreString());
            resetRound();
        });
        btnB.setOnAction(e -> {
            gameModel.addScore(false);
            scoreLabel.setText(gameModel.getScoreString());
            resetRound();
        });

        HBox controls = new HBox(20, btnA, btnB);
        controls.setAlignment(Pos.CENTER);

        root.getChildren().addAll(scoreLabel, powerUpLabel, cardView, btnDraw, controls);

        Scene scene = new Scene(root, 900, 800);
        primaryStage.setTitle("LOCK-IN Companion");
        primaryStage.show();
    }

    private void handleDraw() {
        // Trigger the flip animation on the View
        cardView.flipToWord(gameModel.drawWord(), () -> {
            // When flip is done (card is hidden), update the dice roll text
            String diceResult = gameModel.rollDice();
            powerUpLabel.setText(diceResult);
            if (diceResult.contains("ROLL 6")) {
                powerUpLabel.setStyle("-fx-text-fill: #ff5555; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else {
                powerUpLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 18px; -fx-font-style: italic;");
            }
        });
    }

    private void resetRound() {
        cardView.resetToBack();
        powerUpLabel.setText("Roll the Die to Start");
        powerUpLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-size: 18px; -fx-font-style: italic;");
    }

    private void styleButton(Button b, String color) {
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: #0f0f1b; -fx-font-weight: bold; -fx-padding: 10 20;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}