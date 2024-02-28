package com.example.laborator7.controllerGui.allert;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UserAlert {
    public static void showMessage(Stage owner, Alert.AlertType alertType, String title , String description){
        Alert message=new Alert(alertType);
        message.setHeaderText(title);
        message.setContentText(description);
        message.initOwner(owner);
        message.showAndWait();
    }
    public static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("ErrorMessage");
        message.setContentText(text);
        message.showAndWait();
    }
}
