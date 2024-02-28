package com.example.laborator7;

import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.User;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.validator.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditUserController {
    @FXML
    private TextField textFieldId;
    @FXML
     TextField textFieldFirstName;;
    @FXML
     TextField textFieldLastName;
    @FXML
    TextField textFieldPassword;
    @FXML
   // private TextField textFieldId;
    private ServiceDbForUser service;
    Stage dialogStage;
    User user;
    String password;
    @FXML
    private void initialize() {
    }
    public void setService(ServiceDbForUser service,  Stage stage, User u,String password) {
        this.service = service;
        this.dialogStage=stage;
        this.user=u;
        this.password=password;
        if (null != u) {
            setFields(u);
            textFieldId.setEditable(false);
        }
    }
    private void setFields(User u)
    {
        textFieldId.setText(u.getId().toString());
        textFieldFirstName.setText(u.getFirstName());
        textFieldLastName.setText(u.getLastName());
        //textFieldPassword.setText(u.getPassword());
    }
    @FXML
    public void handleSave(){
        String id=textFieldId.getText();
        String firstNameText=textFieldFirstName.getText();
        String lastNameText=textFieldLastName.getText();
        //String passwordText=textFieldPassword.getText();
        User u=new User(firstNameText,lastNameText,password);

        if (null == this.user)
            saveUser(u);
        else {
            u.setId(Long.parseLong(id));
            updateUser(u);
        }
    }
    private void saveUser(User u)
    {

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
        textFieldId.setText("");
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }

}
