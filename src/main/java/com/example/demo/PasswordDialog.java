package com.example.demo;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class PasswordDialog extends Dialog<String> {

    private PasswordField passwordField;

    public PasswordDialog() {
        setTitle("Passwort-Dialog");
        setHeaderText("Geben Sie Ihr Passwort ein:");

        // Set the result converter to return the entered password
        setResultConverter(new Callback<ButtonType, String>() {
            @Override
            public String call(ButtonType buttonType) {
                if (buttonType == ButtonType.OK) {
                    return passwordField.getText();
                }
                return null;
            }
        });

        // Create the password input field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Create the dialog layout and add the password input field
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(passwordField, 0, 0);

        // Set the dialog content to the layout
        getDialogPane().setContent(gridPane);

        // Set the OK button as the default button
        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(passwordField.textProperty().isEmpty());
    }
}
