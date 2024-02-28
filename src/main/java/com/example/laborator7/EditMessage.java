package com.example.laborator7;


import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.User;
import com.example.laborator7.service.serviceForPerson.ServiceMessageDB;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.validator.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EditMessage {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldDesc;
    @FXML
    private TextField textFieldFrom;
    @FXML
    private TextField textFieldTo;
    @FXML
    private TextArea textAreaMessage;
    @FXML
    private DatePicker datePickerDate;

    private ServiceMessageDB service;
    private ServiceDbForUser serviceDbForUser;
    Stage dialogStage;
    Message message;

    @FXML
    private void initialize() {
    }

    public void setService(ServiceMessageDB service,ServiceDbForUser serviceDbForUser , Stage stage, Message m) {
        this.service = service;
        this.serviceDbForUser = serviceDbForUser;
        this.dialogStage=stage;
        this.message=m;
        if (null != m) {
            setFields(m);
            textFieldId.setEditable(false);
        }
    }

    @FXML
    public void handleSave(){
        String id=textFieldId.getText();
        String from=textFieldFrom.getText();
        User fromUser=serviceDbForUser.findOneServiceUser(from);
        String to=textFieldTo.getText();
        String message=textAreaMessage.getText();
        String[] parts = to.split(",");
        List<User>users=new ArrayList<>();
//// Parsare și afișare nume pentru fiecare element în array
        for (String name : parts) {
            String[] nameParts = name.split(" ");

            // Verifică dacă există exact două părți în nume (lastname și firstname)
            if (nameParts.length == 2) {
                String lastName = nameParts[0];
                String firstName = nameParts[1];
            }
            User u1=serviceDbForUser.findOneServiceUser(name);
            users.add(u1);

        }
        Message m=new Message(fromUser,users, LocalDateTime.now(),message);
            saveMessage(m);
    }

    private void updateMessage(Message m)
    {
        try {
            Message r= this.service.updateMessage(m);
            if (r==null)
                UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare mesaj","Mesajul a fost modificat");
        } catch (ValidationException e) {
            UserAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();
    }


    private void saveMessage(Message m)
    {

        try {
            Message r= this.service.addMessage(m);
            if (r==null)
                dialogStage.close();
            UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"Slavare mesaj","Mesajul a fost salvat");
        } catch (ValidationException e) {
            UserAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();

    }

    private void clearFields() {
        textFieldId.setText("");
        textFieldDesc.setText("");
        textFieldFrom.setText("");
        textFieldTo.setText("");
        textAreaMessage.setText("");
    }
    private void setFields(Message s)
    {
        textFieldId.setText(String.valueOf(s.getId()));
        User f=s.getFrom();
        String from=f.getFirstName()+" "+f.getLastName();
        textFieldFrom.setText(from);
        List<User> to=s.getAll();
        String nameTo="";
        for(User u:to){
            String to1=u.getFirstName()+" "+u.getLastName();
            nameTo=nameTo+to1+" ";
        }
        textFieldTo.setText(nameTo);
        textAreaMessage.setText(s.getMessage());
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
