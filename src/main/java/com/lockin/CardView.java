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
import java.io.InputStream;

public class CardView extends StackPane {

    private Label wordLabel = new Label();

    // VISUALS: Shapes (Backup) + Images (Primary)
    private Rectangle backRect = new Rectangle(350, 500, Color.DARKBLUE);
    private Rectangle frontRect = new Rectangle(350, 500, Color.GOLD);
    private ImageView backView = new ImageView();
    private ImageView frontView = new ImageView();

    private StackPane backLayer;
    private StackPane frontLayer;

    public CardView() {
        this.setPrefSize(350, 500);
        this.setMinSize(350, 500); // Prevents collapse

        loadImages();

        // Setup Layers
        backLayer = new StackPane(backRect, backView);
        frontLayer = new StackPane(frontRect, frontView, wordLabel);
        frontLayer.setVisible(false); // Start hidden

        // Setup Word
        wordLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: #1a1a2e;");

        this.getChildren().addAll(backLayer, frontLayer);
    }

    private void loadImages() {
        try {
            InputStream backStream = getClass().getResourceAsStream("/images/card_back.jpg");
            if (backStream != null) {
                backView.setImage(new Image(backStream));
                backView.setFitWidth(350);
                backView.setPreserveRatio(true);
            }

            InputStream frontStream = getClass().getResourceAsStream("/images/card_front.png");
            if (frontStream != null) {
                frontView.setImage(new Image(frontStream));
                frontView.setFitWidth(350);
                frontView.setPreserveRatio(true);
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    public void flipToWord(String word, Runnable onFlipFinished) {
        // 1. Rotate 90 degrees
        RotateTransition rot = new RotateTransition(Duration.millis(300), this);
        rot.setAxis(Rotate.Y_AXIS);
        rot.setFromAngle(0);
        rot.setToAngle(90);

        rot.setOnFinished(e -> {
            // 2. Swap Content
            backLayer.setVisible(false);
            frontLayer.setVisible(true);
            wordLabel.setText(word);

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
        backLayer.setVisible(true);
        frontLayer.setVisible(false);
        this.setRotate(0);
    }
}