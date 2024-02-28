package com.example.laborator7;

import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.CreatedTuple;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.RequestFriendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.service.ServiceDbForRequest;
import com.example.laborator7.service.serviceForPerson.ServiceDbForFriendship;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.validator.ValidationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditRequestController {
    @FXML
    private TextField textFieldId;
    @FXML
    TextField textFieldFirstName;;
    @FXML
    TextField textFieldLastName;
    @FXML
    // private TextField textFieldId;
    private ServiceDbForFriendship service;
    private ServiceDbForRequest serviceRequest;
    Stage dialogStage;
    User u;
    Friendship friendship;
    @FXML
    private void initialize() {
    }

    public void setService(ServiceDbForFriendship service,ServiceDbForRequest serviceRequest,  Stage stage, User u) {
        this.service = service;
        this.serviceRequest = serviceRequest;
        this.dialogStage=stage;
        this.u=u;
        if (null != u) {
            setFields(u);
            textFieldId.setEditable(false);
        }
    }
    private void setFields(User u)
    {
        textFieldId.setText(u.getId().toString());

    }
    @FXML
    public void handleSave(){
        String id=textFieldId.getText();
        String firstNameText=textFieldFirstName.getText();
        String lastNameText=textFieldLastName.getText();
        User u=service.findName(firstNameText+" "+lastNameText);
        //CreatedTuple<Long,Long> friend=new CreatedTuple<>(Long.parseLong(id),u.getId());
        Friendship f=new Friendship(Long.parseLong(id),u.getId());
        RequestFriendship r=new RequestFriendship(Long.parseLong(id),u.getId(),f.getDate());

        serviceRequest.addServiceUser(r);
        dialogStage.close();
        UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"User with name "+firstNameText+" "+lastNameText+"you have a message","rejected");

        //saveFriendship(f);
    }
//    private void saveFriendship(Friendship u)
//    {
//        // TODO
//        try {
//            boolean r= this.service.addServiceFriendship(u);
//            if (r==false)
//                dialogStage.close();
//            UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"Save Friendship","Mesajul a fost salvat");
//        } catch (ValidationException e) {
//            UserAlert.showErrorMessage(null,e.getMessage());
//        }
//        dialogStage.close();
//
//    }
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
