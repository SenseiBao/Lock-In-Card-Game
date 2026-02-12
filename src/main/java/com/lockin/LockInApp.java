package com.lockin;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

        // FORCE DARK BACKGROUND (The safety fix that works on your 4K screen)
        root.setBackground(new Background(new BackgroundFill(Color.rgb(15, 15, 27), null, null)));

        // Labels
        scoreLabel.setText(gameModel.getScoreString());
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        powerUpLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-size: 18px; -fx-font-style: italic;");

        // --- Buttons ---
        Button btnA = new Button("Team A Point");
        Button btnB = new Button("Team B Point");
        Button btnDraw = new Button("Draw Card");
        Button btnReset = new Button("RESET GAME"); // <--- NEW

        // Button Styling
        String pointStyle = "-fx-background-color: #4ecca3; -fx-text-fill: #0f0f1b; -fx-font-weight: bold; -fx-padding: 10 20;";
        String drawStyle = "-fx-background-color: #d4af37; -fx-text-fill: #0f0f1b; -fx-font-weight: bold; -fx-padding: 10 20;";
        String resetStyle = "-fx-background-color: #ff5555; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;";

        btnA.setStyle(pointStyle);
        btnB.setStyle(pointStyle);
        btnDraw.setStyle(drawStyle);
        btnReset.setStyle(resetStyle);

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

        // NEW: Reset Logic
        btnReset.setOnAction(e -> {
            gameModel.resetGame();
            scoreLabel.setText(gameModel.getScoreString());
            resetRound();
        });

        // Add buttons to a container
        HBox controls = new HBox(20, btnA, btnB, btnReset);
        controls.setAlignment(Pos.CENTER);

        // Add everything to the root layout
        root.getChildren().addAll(scoreLabel, powerUpLabel, cardView, btnDraw, controls);

        Scene scene = new Scene(root, 900, 800);
        primaryStage.setTitle("LOCK-IN Companion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleDraw() {
        // Ask the View to flip, then update the label when it finishes
        cardView.flipToWord(gameModel.drawWord(), () -> {
            String diceResult = gameModel.rollDice();
            powerUpLabel.setText(diceResult);

            // Change color if it's a Power-Up (Roll 6)
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

    public static void main(String[] args) {
        launch(args);
    }
}