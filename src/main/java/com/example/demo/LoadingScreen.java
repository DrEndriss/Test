package com.example.demo;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingScreen {
    private final Stage loadingStage;
    private final ProgressIndicator progressIndicator;

    public LoadingScreen(Stage ownerStage) {
        loadingStage = new Stage();
        loadingStage.initOwner(ownerStage);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.initStyle(StageStyle.UNDECORATED);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1); // Indeterminate progress

        StackPane root = new StackPane(progressIndicator);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root);
        loadingStage.setScene(scene);
    }

    public void show() {
        loadingStage.show();
    }

    public void hide() {
        loadingStage.hide();
    }

    public void runTask(Task<?> task) {
        show();

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                hide();
            }
        });

        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                hide();
            }
        });

        new Thread(task).start();
    }
}

