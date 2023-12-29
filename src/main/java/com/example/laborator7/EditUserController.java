package com.example.laborator7.controllerGui;

import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.User;
import com.example.laborator7.service.ServiceDbForUser;
import com.example.laborator7.service.ServiceForDatabase;
import com.example.laborator7.validator.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class EditUserController {
    @FXML
     TextField textFieldFirstName;;
    @FXML
     TextField textFieldLastName;
    @FXML
   // private TextField textFieldId;
    private ServiceDbForUser service;
    Stage dialogStage;
    User user;
    @FXML
    private void initialize() {
    }
    public void setService(ServiceDbForUser service,  Stage stage, User u) {
        this.service = service;
        this.dialogStage=stage;
        this.user=u;
        if (null != u) {
            setFields(u);
            //textFieldId.setEditable(false);
        }
    }
    private void setFields(User u)
    {
        //textFieldId.setText(u.getId().toString());
        textFieldFirstName.setText(u.getFirstName());
        textFieldLastName.setText(u.getLastName());
    }
    @FXML
    public void handleSave(){
        //String id=textFieldId.getText();
        String firstNameText=textFieldFirstName.getText();
        String lastNameText=textFieldLastName.getText();

        User u=new User(firstNameText,lastNameText);
        if (null == this.user)
            saveUser(u);
        else
            updateUser(u);
    }
    private void saveUser(User u)
    {
        // TODO
        try {
            User r= this.service.addServiceUser(u);
            if (r==null)
                dialogStage.close();
            UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"Slavare mesaj","Mesajul a fost salvat");
        } catch (ValidationException e) {
            UserAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();

    }
    private void updateUser(User m)
    {
        try {
            User r= this.service.updateServiceUser(m);
            if (user==null)
                UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare mesaj","Mesajul a fost modificat");
        } catch (ValidationException e) {
            UserAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();
    }
    private void clearFields() {
        //textFieldId.setText("");
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
    }
    @FXML
    public void handleCancel(){
        dialogStage.close();
    }

}
