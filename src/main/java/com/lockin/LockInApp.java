package com.lockin;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Random;

public class LockInApp extends Application {

    private GameModel gameModel = new GameModel();
    private CardView cardView;
    private Label scoreLabel = new Label();
    private Label powerUpLabel = new Label("Roll the Die to Start");

    private ListView<String> listA = new ListView<>();
    private ListView<String> listB = new ListView<>();

    private String currentWord = null;
    private Random random = new Random();

    // --- SOUND ARRAYS ---
    private AudioClip[] drawSounds = new AudioClip[2];
    private AudioClip[] pointSounds = new AudioClip[4];

    @Override
    public void start(Stage primaryStage) {
        cardView = new CardView();
        loadSounds();

        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(15, 15, 27), null, null)));
        root.setPadding(new Insets(20));

        // --- TOP SECTION ---
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        scoreLabel.setText(gameModel.getScoreString());
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        powerUpLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-size: 18px; -fx-font-style: italic;");
        topBox.getChildren().addAll(scoreLabel, powerUpLabel);
        root.setTop(topBox);

        // --- CENTER SECTION ---
        StackPane centerContainer = new StackPane(cardView);
        centerContainer.setPadding(new Insets(20));
        root.setCenter(centerContainer);

        // --- SIDE LISTS ---
        root.setLeft(createTeamBox("TEAM A", listA, "#4ecca3"));
        root.setRight(createTeamBox("TEAM B", listB, "#4ecca3"));

        // --- BOTTOM SECTION ---
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(20, 0, 0, 0));

        Button btnA = new Button("Team A Point");
        Button btnB = new Button("Team B Point");
        Button btnDraw = new Button("Draw Card");
        Button btnReset = new Button("RESET GAME");

        String pointStyle = "-fx-background-color: #4ecca3; -fx-text-fill: #0f0f1b; -fx-font-weight: bold; -fx-padding: 10 20;";
        String drawStyle = "-fx-background-color: #d4af37; -fx-text-fill: #0f0f1b; -fx-font-weight: bold; -fx-padding: 10 20;";
        String resetStyle = "-fx-background-color: #ff5555; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;";

        btnA.setStyle(pointStyle);
        btnB.setStyle(pointStyle);
        btnDraw.setStyle(drawStyle);
        btnReset.setStyle(resetStyle);

        btnDraw.setOnAction(e -> handleDraw());
        btnA.setOnAction(e -> handleWin(true));
        btnB.setOnAction(e -> handleWin(false));
        btnReset.setOnAction(e -> {
            gameModel.resetGame();
            updateUI();
            resetRound();
        });

        controls.getChildren().addAll(btnA, btnB, btnDraw, btnReset);
        root.setBottom(controls);

        Scene scene = new Scene(root, 1100, 850);
        primaryStage.setTitle("LOCK-IN Companion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadSounds() {
        try {
            // Load Draw Sounds (50/50 chance later)
            drawSounds[0] = loadAudio("/sounds/draw.wav");
            drawSounds[1] = loadAudio("/sounds/draw2.wav");

            // Load Point Sounds (80% for index 0, 20% shared for 1-3)
            pointSounds[0] = loadAudio("/sounds/point.mp3");
            pointSounds[1] = loadAudio("/sounds/point2.mp3");
            pointSounds[2] = loadAudio("/sounds/point3.mp3"); // Using the extension as specified
            pointSounds[3] = loadAudio("/sounds/point4.mp3");

        } catch (Exception e) {
            System.err.println("Error loading sound effects: " + e.getMessage());
        }
    }

    private AudioClip loadAudio(String path) {
        URL url = getClass().getResource(path);
        return (url != null) ? new AudioClip(url.toExternalForm()) : null;
    }

    private void handleDraw() {
        // Play random draw sound (50/50)
        AudioClip sfx = drawSounds[random.nextInt(2)];
        if (sfx != null) sfx.play();

        currentWord = gameModel.drawWord();
        cardView.flipToWord(currentWord, () -> {
            String diceResult = gameModel.rollDice();
            powerUpLabel.setText(diceResult);
            if (diceResult.contains("ROLL 6")) {
                powerUpLabel.setStyle("-fx-text-fill: #ff5555; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else {
                powerUpLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 18px; -fx-font-style: italic;");
            }
        });
    }

    private void handleWin(boolean isTeamA) {
        if (currentWord != null && !currentWord.equals("GAME OVER")) {

            // --- POINT SFX LOGIC ---
            double chance = random.nextDouble();
            if (chance < 0.8) {
                // 80% chance for point.mp3
                if (pointSounds[0] != null) pointSounds[0].play();
            } else {
                // 20% chance for one of the others (point2, point3, point4)
                int extraIndex = 1 + random.nextInt(3); // Picks index 1, 2, or 3
                if (pointSounds[extraIndex] != null) pointSounds[extraIndex].play();
            }

            gameModel.recordWin(isTeamA, currentWord);
            updateUI();
            resetRound();
        }
    }

    private VBox createTeamBox(String title, ListView<String> list, String color) {
        VBox box = new VBox(10);
        box.setPrefWidth(200);
        box.setAlignment(Pos.TOP_CENTER);
        Label lbl = new Label(title);
        lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 18px;");
        list.setStyle("-fx-control-inner-background: #1a1a2e; -fx-background-color: #1a1a2e; -fx-text-fill: white;");
        VBox.setVgrow(list, Priority.ALWAYS);
        box.getChildren().addAll(lbl, list);
        return box;
    }

    private void updateUI() {
        scoreLabel.setText(gameModel.getScoreString());
        listA.getItems().clear();
        listA.getItems().addAll(gameModel.getWords(true));
        listB.getItems().clear();
        listB.getItems().addAll(gameModel.getWords(false));
    }

    private void resetRound() {
        cardView.resetToBack();
        currentWord = null;
        powerUpLabel.setText("Roll the Die to Start");
        powerUpLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-size: 18px; -fx-font-style: italic;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}