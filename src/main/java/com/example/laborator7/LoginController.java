package com.example.laborator7;

import com.example.laborator7.EditUserController;
import com.example.laborator7.UserController;
import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.FriendDataBaseRepository;
import com.example.laborator7.repository.RequestPagingRepository;
import com.example.laborator7.repository.UserDBPagingRepository;
import com.example.laborator7.service.ServiceDbForRequest;
import com.example.laborator7.service.serviceForPerson.ServiceDbForFriendship;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.service.serviceForPerson.ServiceMessageDB;
import com.example.laborator7.validator.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginController {
    private Validator<User> userValidator;
    private UserDBPagingRepository userRepository;
    private FriendDataBaseRepository friendRepository;
    private RequestPagingRepository requestRepository;
    private ServiceMessageDB serviceMessageDB;
    //private Node initial;
    private ServiceDbForFriendship serviceDbForFriendship;
    private ServiceDbForRequest serviceDbForRequest;
    private ServiceDbForUser service;
    @FXML
    public AnchorPane root;
    @FXML
    public BorderPane borderPane;
    @FXML
    public TextField textFieldUsername;
    @FXML
    public PasswordField textFieldPassword;
    @FXML
    public Button buttonLogin;
    @FXML
    public Button buttonRegister;
    @FXML
    public Button buttonExit;
    private Stage primaryStage;
    @FXML
    public void initialize() {
    }
    public void setUserTaskService(BorderPane borderPane, Stage primaryStage, AnchorPane anchor, UserDBPagingRepository userRepository, FriendDataBaseRepository friendRepository, RequestPagingRepository requestRepository, ServiceDbForUser serviceForDatabase, ServiceDbForFriendship serviceDbForFriendship, ServiceDbForRequest serviceDbForRequest, ServiceMessageDB serviceMessageDB) {
        service = serviceForDatabase;
        this.root=anchor;
        this.borderPane=borderPane;
        this.primaryStage=primaryStage;
        this.serviceDbForFriendship=serviceDbForFriendship;
        this.serviceDbForRequest=serviceDbForRequest;
        this.serviceMessageDB=serviceMessageDB;
        this.userRepository=userRepository;
        this.friendRepository=friendRepository;
        this.requestRepository=requestRepository;
    }
      public  String hashPassword(String password) {
          StringBuilder hexStringBuilder = new StringBuilder();
          try {
              MessageDigest md = MessageDigest.getInstance("SHA-256");
              byte[] inputBytes = password.getBytes();
              byte[] hashBytes = md.digest(inputBytes);
              for (byte b : hashBytes) {
                    hexStringBuilder.append(String.format("%02x", b));
              }
              System.out.println("Textul de criptat: " + password);
              System.out.println("Rezultatul SHA-256: " + hexStringBuilder.toString());
          } catch (NoSuchAlgorithmException e) {
              e.printStackTrace();
          }
          return hexStringBuilder.toString();
      }

    @FXML
    public void handleLogin() throws IOException {
        if(textFieldPassword.getText().equals("") || textFieldUsername.getText().equals("")){
            UserAlert.showErrorMessage(null,"Nu ati introdus datele");
            return;
        }
        String username = textFieldUsername.getText();
        String passwordIntroduced= textFieldPassword.getText();
        String password=hashPassword(passwordIntroduced);
        System.out.println(password);
        User u=service.findUserPassword(password,username);
        if(u==null){
            UserAlert.showErrorMessage(null,"Nu exista userul");
            return;
        }
        FXMLLoader messageLoader = new FXMLLoader();
        messageLoader.setLocation(getClass().getResource("aplicatie-view.fxml"));
        AnchorPane messageTaskLayout = messageLoader.load();
        primaryStage.setScene(new Scene(messageTaskLayout));

        UserController messageTaskController = messageLoader.getController();
        BorderPane pane=(BorderPane)messageTaskLayout.getChildren().get(0);
        messageTaskController.setUserTaskService(pane,primaryStage,messageTaskLayout, userRepository,friendRepository,requestRepository,service,serviceDbForFriendship,serviceDbForRequest,serviceMessageDB,u);
    }
    public void handleRegister(ActionEvent e) throws IOException {

       String username = textFieldUsername.getText();
       String newpassword = textFieldPassword.getText();
       String password=hashPassword(newpassword);

        FXMLLoader messageLoader = new FXMLLoader();
        messageLoader.setLocation(getClass().getResource("edit-user-view.fxml"));
        AnchorPane messageTaskLayout = messageLoader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Message");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(messageTaskLayout);
        dialogStage.setScene(scene);

        EditUserController messageTaskController = messageLoader.getController();
        //BorderPane pane=(BorderPane)messageTaskLayout.getChildren().get(0);
        messageTaskController.setService( service,dialogStage,null,password);
        dialogStage.show();
    }
}
