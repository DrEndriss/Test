package com.example.demo;

import com.example.demo.handler.DBHandler;
import com.example.demo.handler.DataHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.nio.charset.StandardCharsets;


public class Application extends javafx.application.Application {

    private static final String FXML_FILE = "main.fxml";
    private static final String WINDOW_TITLE = "Dr. Endriss";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 800;
    private static final String ICON_PATH = "icon.png"; // Path to your icon file

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource(FXML_FILE));

            // Load the FXML file and retrieve the controller
            Parent root = fxmlLoader.load();
            ApplicationController controller = fxmlLoader.getController();
            controller.setPrimaryStage(stage);

            // Create DBHandler and CSVHandler instances
            DBHandler dbhandler = new DBHandler();
            DataHandler datahandler = new DataHandler();

            // Set the handlers in the controller
            controller.setDatabaseHandler(dbhandler);
            controller.setCSVHandler(datahandler);



            // Load the data from the controller
            boolean dataLoaded = controller.loadData();

            if (dataLoaded) {
                Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
                stage.setScene(scene);
                stage.setTitle(WINDOW_TITLE);

                // Load the CSS file
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

                // Set the application icon
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH)));
                stage.getIcons().add(icon);

                stage.show();
            } else {
                // Alternatively, you can show an error message or take other appropriate actions
                System.out.println("Failed to load data. Starting without data.");

                Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
                stage.setScene(scene);
                stage.setTitle(WINDOW_TITLE);

                // Set the application icon
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH)));
                stage.getIcons().add(icon);

                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
