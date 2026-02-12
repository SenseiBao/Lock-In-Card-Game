package com.lockin;

import javafx.animation.RotateTransition;
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

    // DEBUG MODE: Bright colors to see if images are missing
    private Rectangle backPlaceholder = new Rectangle(350, 500, Color.RED);  // RED = Back Image Missing
    private Rectangle frontPlaceholder = new Rectangle(350, 500, Color.GREEN); // GREEN = Front Image Missing

    private Image backImg;
    private Image frontImg;

    public CardView() {
        this.setPrefSize(350, 500);
        loadAssets();

        wordLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");
        wordLabel.setVisible(false);

        // Add layers
        this.getChildren().addAll(backPlaceholder, frontPlaceholder, backView, frontView, wordLabel);

        showBack();
    }

    private void loadAssets() {
        System.out.println("--- LOADING ASSETS ---");
        try {
            // Debugging the paths
            var backUrl = getClass().getResource("/images/card_back.jpg");
            var frontUrl = getClass().getResource("/images/card_front.png");

            System.out.println("Back URL: " + (backUrl != null ? "FOUND" : "MISSING"));
            System.out.println("Front URL: " + (frontUrl != null ? "FOUND" : "MISSING"));

            if (backUrl != null) {
                backImg = new Image(backUrl.toExternalForm());
                backView.setImage(backImg);
                backView.setFitWidth(350);
                backView.setPreserveRatio(true);
            }
            if (frontUrl != null) {
                frontImg = new Image(frontUrl.toExternalForm());
                frontView.setImage(frontImg);
                frontView.setFitWidth(350);
                frontView.setPreserveRatio(true);
            }
        } catch (Exception e) {
            System.err.println("CRASH LOADING IMAGES: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void flipToWord(String word, Runnable onFlipFinished) {
        RotateTransition rot = new RotateTransition(Duration.millis(300), this);
        rot.setAxis(Rotate.Y_AXIS);
        rot.setFromAngle(0);
        rot.setToAngle(90);

        rot.setOnFinished(e -> {
            showFront(word);
            if (onFlipFinished != null) onFlipFinished.run();

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
        // If image exists, show it. If not, show RED rectangle.
        boolean hasBack = (backImg != null && !backImg.isError());
        backView.setVisible(hasBack);
        backPlaceholder.setVisible(!hasBack);

        frontView.setVisible(false);
        frontPlaceholder.setVisible(false);
        wordLabel.setVisible(false);
    }

    private void showFront(String word) {
        // If image exists, show it. If not, show GREEN rectangle.
        boolean hasFront = (frontImg != null && !frontImg.isError());
        frontView.setVisible(hasFront);
        frontPlaceholder.setVisible(!hasFront);

        backView.setVisible(false);
        backPlaceholder.setVisible(false);

        wordLabel.setText(word);
        wordLabel.setVisible(true);
    }
}