package com.lockin;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LockInApp extends Application {

    private GameModel gameModel = new GameModel();
    private CardView cardView;
    private Label scoreLabel = new Label();
    private Label powerUpLabel = new Label("Roll the Die to Start");

    // NEW: Lists to display won words
    private ListView<String> listA = new ListView<>();
    private ListView<String> listB = new ListView<>();

    // Track current word so we know what to add to the list
    private String currentWord = null;

    @Override
    public void start(Stage primaryStage) {
        cardView = new CardView();

        // --- ROOT LAYOUT (BorderPane is better for Side Panels) ---
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(15, 15, 27), null, null)));
        root.setPadding(new Insets(20));

        // --- TOP SECTION (Scores & Status) ---
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);

        scoreLabel.setText(gameModel.getScoreString());
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        powerUpLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-size: 18px; -fx-font-style: italic;");

        topBox.getChildren().addAll(scoreLabel, powerUpLabel);
        root.setTop(topBox);

        // --- CENTER SECTION (The Card) ---
        StackPane centerContainer = new StackPane(cardView);
        centerContainer.setPadding(new Insets(20)); // Breathing room
        root.setCenter(centerContainer);

        // --- SIDE LISTS ---
        VBox leftBox = createTeamBox("TEAM A", listA, "#4ecca3");
        VBox rightBox = createTeamBox("TEAM B", listB, "#4ecca3"); // Or maybe specific team colors?

        root.setLeft(leftBox);
        root.setRight(rightBox);

        // --- BOTTOM SECTION (Controls) ---
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

        // --- EVENT WIRING ---
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

        Scene scene = new Scene(root, 1100, 850); // Made wider to fit lists
        primaryStage.setTitle("LOCK-IN Companion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createTeamBox(String title, ListView<String> list, String color) {
        VBox box = new VBox(10);
        box.setPrefWidth(200);
        box.setAlignment(Pos.TOP_CENTER);

        Label lbl = new Label(title);
        lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 18px;");

        list.setStyle("-fx-control-inner-background: #1a1a2e; -fx-background-color: #1a1a2e; -fx-text-fill: white;");
        VBox.setVgrow(list, Priority.ALWAYS); // Fill height

        box.getChildren().addAll(lbl, list);
        return box;
    }

    private void handleDraw() {
        currentWord = gameModel.drawWord(); // Capture the word!
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
            gameModel.recordWin(isTeamA, currentWord);
            updateUI();
            resetRound();
        }
    }

    private void updateUI() {
        scoreLabel.setText(gameModel.getScoreString());

        // Refresh lists
        listA.getItems().clear();
        listA.getItems().addAll(gameModel.getWords(true));

        listB.getItems().clear();
        listB.getItems().addAll(gameModel.getWords(false));
    }

    private void resetRound() {
        cardView.resetToBack();
        currentWord = null; // Clear current word so you can't double score
        powerUpLabel.setText("Roll the Die to Start");
        powerUpLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-size: 18px; -fx-font-style: italic;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}