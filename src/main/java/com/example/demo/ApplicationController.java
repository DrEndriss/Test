package com.example.demo;

import com.example.demo.handler.DBHandler;
import com.example.demo.handler.DataHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;



public class ApplicationController implements Initializable {
    @FXML
    private BorderPane layout;
    @FXML
    private TextField filterTextField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private Button searchButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button exportButtonExcel;
    @FXML
    private TableView<String[]> table;
    @FXML
    private Label notificationLabel;

    private ObservableList<String[]> tableData;
    private List<String[]> allData;
    private List<TableColumn<String[], String>> tableColumns;
    private DBHandler dbhandler;
    private DataHandler csvhandler;
    private String filePath;

    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableData = FXCollections.observableArrayList();
        table.setItems(tableData);

        filterComboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = filterTextField.getText();
            int columnIndex = newValue.intValue();
        });

        searchButton.setOnAction(e -> {
            String searchText = filterTextField.getText();
            int columnIndex = filterComboBox.getSelectionModel().getSelectedIndex();
            List<String[]> searchResult = searchRows(allData, searchText, columnIndex);
            tableData.setAll(searchResult);
        });

        resetButton.setOnAction(e -> {
            filterTextField.clear();
            filterComboBox.getSelectionModel().clearSelection();
            tableData.setAll(allData);
            filterComboBox.getSelectionModel().selectFirst();
        });

        exportButton.setOnAction(e -> {
            if (csvhandler.exportToCSV(table, allData, tableData)) {
                System.out.println("Export erfolgreich.");
            } else {
                System.err.println("Export gescheitert.");
            }
        });

        // Apply custom CSS to the buttons
        searchButton.getStyleClass().add("custom-button");
        resetButton.getStyleClass().add("custom-button");
        exportButton.getStyleClass().add("custom-button");
        refreshButton.getStyleClass().add("custom-button");
        exportButtonExcel.getStyleClass().add("custom-button");

        // Enable cell selection for copying text
        table.getSelectionModel().setCellSelectionEnabled(true);
        MenuItem copyMenuItem = new MenuItem("Copy");
        copyMenuItem.setOnAction(event -> copySelectedCellsToClipboard());
        table.setContextMenu(new ContextMenu(copyMenuItem));
    }

    private void copySelectedCellsToClipboard() {
        ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
        StringBuilder sb = new StringBuilder();

        for (TablePosition position : selectedCells) {
            int row = position.getRow();
            int col = position.getColumn();

            String cellValue = tableData.get(row)[col];
            sb.append(cellValue);
            sb.append("\t"); // Use tab as delimiter between cells
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(sb.toString().trim());
        clipboard.setContent(content);
    }

    private Stage loadingStage; // Declare loadingStage as a class member

    @FXML
    private void handleExportToExcel(ActionEvent event) {
        if (csvhandler.exportToExcel(table, allData, tableData)) {
            System.out.println("Export successful.");
        } else {
            System.err.println("Export failed.");
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        // Create a ChoiceDialog to select the query
        ChoiceDialog<String> dialog = new ChoiceDialog<>("OP Teilnehmer", "Teilnehmer", "Belegung");
        dialog.getItems().add(0, "OP Teilnehmer"); // Add "Query 1" at the beginning of the list
        dialog.setSelectedItem("OP Teilnehmer"); // Set "Query 1" as the default selected item
        dialog.setTitle("Abfrage auswählen");
        dialog.setHeaderText("Wählen Sie eine  zur Ausführung");
        dialog.setContentText("Abfrage:");

        Optional<String> queryResult = dialog.showAndWait();

        if (queryResult.isPresent()) {
            String selectedQuery = queryResult.get();

            // Create a custom DialogPane for the username and password input
            DialogPane dialogPane = new DialogPane();
            dialogPane.setHeaderText("Geben Sie Ihren Benutzernamen und Ihr Passwort ein");


            // Create the Username and Password text fields
            TextField usernameField = new TextField();
            PasswordField passwordField = new PasswordField();

            // Add the text fields to the dialog pane
            dialogPane.setContent(new VBox(10, new Label("Benutzername:"), usernameField, new Label("Passwort:"), passwordField));

            // Set the dialog pane as the content of the dialog
            Dialog<ButtonType> usernameDialog = new Dialog<>();
            usernameDialog.setDialogPane(dialogPane);
            usernameDialog.setTitle("Datenbank Credentials");

            // Create the execute button
            ButtonType executeButtonType = new ButtonType("Execute", ButtonBar.ButtonData.OK_DONE);
            usernameDialog.getDialogPane().getButtonTypes().addAll(executeButtonType, ButtonType.CANCEL);

            // Enable/disable the execute button based on username and password input
            Node executeButton = usernameDialog.getDialogPane().lookupButton(executeButtonType);
            executeButton.setDisable(true);
            usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
                executeButton.setDisable(newValue.trim().isEmpty() || passwordField.getText().isEmpty());
            });
            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                executeButton.setDisable(newValue.trim().isEmpty() || usernameField.getText().isEmpty());
            });

            // Show the username and password dialog
            Optional<ButtonType> usernameResult = usernameDialog.showAndWait();

            if (usernameResult.isPresent() && usernameResult.get() == executeButtonType) {
                String username = usernameField.getText();
                String password = passwordField.getText();

                // Create a Task to load the data from the database
                Task<List<String[]>> loadDataTask = new Task<List<String[]>>() {
                    @Override
                    protected List<String[]> call() throws Exception {
                        // Simulate a delay to show the loading screen
                        Thread.sleep(2000);
                        System.out.println("START");
                        // Create the DB request using the username, password, and selected query
                        int queryIndex; // Convert selectedQuery to int
                        if (selectedQuery == "OP Teilnehmer"){
                            queryIndex = 1;
                        }else if(selectedQuery == "Teilnehmer"){
                            queryIndex = 2;
                        }else if(selectedQuery == "Belegung"){
                            queryIndex = 3;
                        }else{
                            queryIndex = 1;
                        }
                        System.out.println(queryIndex);
                        return dbhandler.executeQuery(username, password, queryIndex);
                    }
                };

                showLoadingScreen(loadDataTask);

                loadDataTask.setOnSucceeded(workerEvent -> {
                    List<String[]> updatedData = loadDataTask.getValue();
                    if (!updatedData.isEmpty()) {
                        allData = updatedData;
                        updateTableData();
                        notificationLabel.setText("Daten erfolgreich aktualisiert.");

                        String filePath = "data.csv";
                        if (csvhandler.writeToCsv(allData, tableData, filePath)) {
                            System.out.println("Data exported to " + filePath + " successfully.");
                        } else {
                            System.err.println("Failed to export data to " + filePath + ".");
                        }
                    } else {
                        notificationLabel.setText("Daten können nicht aktualisiert werden.");
                    }
                    hideLoadingScreen();
                });

                loadDataTask.setOnFailed(workerEvent -> {
                    notificationLabel.setText("Failed to update data."); // Display failure notification
                    hideLoadingScreen();
                });

                // Start the task
                Thread loadDataThread = new Thread(loadDataTask);
                loadDataThread.start();
            }

        }
        }


    private void showLoadingScreen(Task<?> task) {
        // Create a new stage for the loading screen if not already created
        if (loadingStage == null) {
            loadingStage = new Stage();
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            loadingStage.initStyle(StageStyle.UNDECORATED);

            // Create a ProgressBar
            ProgressBar progressBar = new ProgressBar();
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            // Set the ProgressBar as the root of the loading screen
            Scene loadingScene = new Scene(progressBar);
            loadingStage.setScene(loadingScene);
        }

        // Bind the ProgressBar's progress property to the task's progress property
        ProgressBar progressBar = (ProgressBar) loadingStage.getScene().getRoot();
        progressBar.progressProperty().bind(task.progressProperty());

        // Show the loading screen
        loadingStage.show();
    }

    private void hideLoadingScreen() {
        if (loadingStage != null) {
            // Close the loading screen
            loadingStage.close();
        }
    }

    private void loadCSVFile() {
        notificationLabel.setText(""); // Clear the notification label
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Stage stage = (Stage) layout.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            filePath = selectedFile.getAbsolutePath();
            allData = csvhandler.readDataFromCSV(filePath);
            updateTableData();
        }
    }

    private void updateTableData() {
        if (allData != null && !allData.isEmpty()) {
            String[] headerRow = allData.get(0);
            filterComboBox.getItems().setAll(headerRow);
            filterComboBox.getSelectionModel().selectFirst();

            tableColumns = createTableColumns(headerRow);
            table.getColumns().setAll(tableColumns);

            tableData.setAll(allData.subList(1, allData.size()));
        }
    }

    public void setDatabaseHandler(DBHandler dbhandler) {
        this.dbhandler = dbhandler;
    }

    public void setCSVHandler(DataHandler csvhandler) {
        this.csvhandler = csvhandler;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        allData = csvhandler.readDataFromCSV(filePath);
        updateTableData();
    }

    public boolean loadData() {
        allData = new ArrayList<>();

        String filePath = getCSVFilePath();
        if (filePath != null) {
            allData = csvhandler.readDataFromCSV(filePath);
            setFilePath(filePath);
        } else {
            System.err.println("CSV file not found.");
            return false;
        }

        if (allData != null && !allData.isEmpty()) {
            String[] headerRow = allData.get(0);
            filterComboBox.getItems().setAll(headerRow);
            tableColumns = createTableColumns(headerRow);
            table.getColumns().setAll(tableColumns);
            tableData.setAll(allData.subList(1, allData.size()));
            updateTableData();
        }

        return true;
    }

    public String getCSVFilePath() {
        String fileName = "data.csv";
        URL resource = Main.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            System.err.println("CSV file not found: " + fileName);
            return null;
        } else {
            return resource.getPath();
        }
    }

    private List<String[]> searchRows(List<String[]> data, String searchText, int columnIndex) {
        List<String[]> searchResult = new ArrayList<>();
        if (columnIndex >= 0 && columnIndex < data.get(0).length) {
            for (String[] row : data) {
                String cellValue = row[columnIndex];
                if (cellValue != null && cellValue.toLowerCase().contains(searchText.toLowerCase())) {
                    searchResult.add(row);
                }
            }
        }
        return searchResult;
    }

    private List<TableColumn<String[], String>> createTableColumns(String[] headerRow) {
        List<TableColumn<String[], String>> columns = new ArrayList<>();
        for (int i = 0; i < headerRow.length; i++) {
            final int columnIndex = i;
            TableColumn<String[], String> column = new TableColumn<>(headerRow[i]);
            column.setCellValueFactory(cellData -> {
                String[] rowData = cellData.getValue();
                if (columnIndex >= 0 && columnIndex < rowData.length) {
                    return new SimpleStringProperty(rowData[columnIndex]);
                } else {
                    return new SimpleStringProperty("");
                }
            });
            columns.add(column);
        }
        return columns;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
