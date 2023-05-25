package com.example.demo.handler;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoadingIndicator {
    private Stage indicatorStage;

    public void showLoadingIndicator(Scene scene) {
        // Create a new stage for the loading indicator
        indicatorStage = new Stage();
        indicatorStage.initModality(Modality.APPLICATION_MODAL);
        indicatorStage.initOwner(scene.getWindow());
        indicatorStage.setResizable(false);

        // Create a progress indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1); // Indefinite progress
        progressIndicator.setStyle("-fx-progress-color: #005BAC;"); // Set the color of the progress indicator

        // Add the progress indicator to a stack pane
        StackPane stackPane = new StackPane(progressIndicator);
        stackPane.setStyle("-fx-background-color: #F2F2F2;"); // Set the background color of the stack pane

        // Create a new scene for the loading indicator
        Scene indicatorScene = new Scene(stackPane, 100, 100);

        // Set the scene of the indicator stage
        indicatorStage.setScene(indicatorScene);

        // Show the indicator stage
        Platform.runLater(indicatorStage::show);
    }

    public void hideLoadingIndicator() {
        // Hide the indicator stage
        Platform.runLater(indicatorStage::hide);
    }
}
