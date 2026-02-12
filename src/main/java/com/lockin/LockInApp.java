package com.lockin;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LockInApp extends Application {

    private List<String> deck = new ArrayList<>();
    private int teamAScore = 0;
    private int teamBScore = 0;

    private Label wordLabel = new Label("LOCK-IN");
    private Label scoreLabel = new Label("Team A: 0  |  Team B: 0");
    private ImageView cardImageView = new ImageView();
    private boolean isCardFlipped = false;

    // Load images from resources
    private Image backImage;
    private Image frontFrame;

    @Override
    public void start(Stage primaryStage) {
        loadAssets();

        // Main Container
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0f0f1b;"); // Dark theme

        // Scoreboard
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Arial';");

        // The Card Display Area
        StackPane cardContainer = new StackPane();
        cardContainer.setPrefSize(350, 500);

        // Initial state: Show the back
        cardImageView.setImage(backImage);
        cardImageView.setFitWidth(350);
        cardImageView.setPreserveRatio(true);

        // Word Label (Hidden by default or behind the back)
        wordLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");
        wordLabel.setVisible(false);

        cardContainer.getChildren().addAll(cardImageView, wordLabel);

        // Buttons
        HBox scoreButtons = new HBox(20);
        scoreButtons.setAlignment(Pos.CENTER);
        Button btnA = new Button("Team A Point");
        Button btnB = new Button("Team B Point");
        Button btnDraw = new Button("Draw Card");

        btnDraw.setOnAction(e -> drawCard());
        btnA.setOnAction(e -> { teamAScore++; updateUI(); });
        btnB.setOnAction(e -> { teamBScore++; updateUI(); });

        scoreButtons.getChildren().addAll(btnA, btnB);
        root.getChildren().addAll(scoreLabel, cardContainer, btnDraw, scoreButtons);

        Scene scene = new Scene(root, 800, 700);
        primaryStage.setTitle("LOCK-IN Companion");
        primaryStage.show();
    }

    private void loadAssets() {
        try {
            backImage = new Image(getClass().getResourceAsStream("/images/card_back.png"));
            frontFrame = new Image(getClass().getResourceAsStream("/images/card_front.png"));

            // Load words
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream("/words.txt")));
            reader.lines().forEach(deck::add);
            Collections.shuffle(deck);
        } catch (Exception e) {
            System.out.println("Error loading assets: " + e.getMessage());
        }
    }

    private void drawCard() {
        if (!deck.isEmpty()) {
            String word = deck.remove(0);
            wordLabel.setText(word);

            // Toggle to Front
            cardImageView.setImage(frontFrame);
            wordLabel.setVisible(true);
            isCardFlipped = true;
        } else {
            wordLabel.setText("OUT OF CARDS");
        }
    }

    private void updateUI() {
        scoreLabel.setText("Team A: " + teamAScore + "  |  Team B: " + teamBScore);
        // Reset card for next round
        cardImageView.setImage(backImage);
        wordLabel.setVisible(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}