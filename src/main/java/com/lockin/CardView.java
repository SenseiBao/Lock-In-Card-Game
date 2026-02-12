package com.lockin;

import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class CardView extends StackPane {

    private ImageView backView = new ImageView();
    private ImageView frontView = new ImageView();
    private Label wordLabel = new Label();

    // Fallback shapes in case images fail
    private Rectangle backPlaceholder = new Rectangle(350, 500, Color.DARKBLUE);
    private Rectangle frontPlaceholder = new Rectangle(350, 500, Color.WHITESMOKE);

    private Image backImg;
    private Image frontImg;

    public CardView() {
        this.setPrefSize(350, 500);
        loadAssets();

        // Setup Word Label
        wordLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");
        wordLabel.setVisible(false);

        // Add everything to the stack (Layers)
        // Layer 1: Placeholders (Only visible if images fail)
        // Layer 2: Images
        // Layer 3: Text
        this.getChildren().addAll(backPlaceholder, frontPlaceholder, backView, frontView, wordLabel);

        showBack(); // Start face down
    }

    private void loadAssets() {
        try {
            // NOTE: Make sure these match your file names exactly (case sensitive!)
            var backStream = getClass().getResourceAsStream("/images/card_back.jpg");
            var frontStream = getClass().getResourceAsStream("/images/card_front.png");

            if (backStream != null) {
                backImg = new Image(backStream);
                backView.setImage(backImg);
                backView.setFitWidth(350);
                backView.setPreserveRatio(true);
                backPlaceholder.setVisible(false); // Hide placeholder if image loads
            }

            if (frontStream != null) {
                frontImg = new Image(frontStream);
                frontView.setImage(frontImg);
                frontView.setFitWidth(350);
                frontView.setPreserveRatio(true);
                frontPlaceholder.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("Could not load images: " + e.getMessage());
        }
    }

    public void flipToWord(String word, Runnable onFlipFinished) {
        // 1. Rotate to 90 degrees
        RotateTransition rot = new RotateTransition(Duration.millis(300), this);
        rot.setAxis(Rotate.Y_AXIS);
        rot.setFromAngle(0);
        rot.setToAngle(90);

        rot.setOnFinished(e -> {
            // 2. Swap Content
            showFront(word);
            if (onFlipFinished != null) onFlipFinished.run();

            // 3. Rotate back to 0
            RotateTransition rotBack = new RotateTransition(Duration.millis(300), this);
            rotBack.setAxis(Rotate.Y_AXIS);
            rotBack.setFromAngle(90);
            rotBack.setToAngle(0);
            rotBack.play();
        });
        rot.play();
    }

    public void resetToBack() {
        showBack();
    }

    private void showBack() {
        backView.setVisible(true);
        backPlaceholder.setVisible(backImg == null); // Show blue rect if image missing

        frontView.setVisible(false);
        frontPlaceholder.setVisible(false);
        wordLabel.setVisible(false);
    }

    private void showFront(String word) {
        backView.setVisible(false);
        backPlaceholder.setVisible(false);

        frontView.setVisible(true);
        frontPlaceholder.setVisible(frontImg == null); // Show white rect if image missing

        wordLabel.setText(word);
        wordLabel.setVisible(true);
    }
}