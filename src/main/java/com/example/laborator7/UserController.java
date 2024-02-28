package com.example.laborator7;


import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.Entity;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.*;
import com.example.laborator7.service.ServiceDbForRequest;
import com.example.laborator7.service.serviceForPerson.ServiceMessageDB;
import com.example.laborator7.service.serviceForPerson.ServiceDbForFriendship;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.utils.events.user.UserTaskChangeEvent;
import com.example.laborator7.utils.observer.Observer;
import com.example.laborator7.validator.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserTaskChangeEvent> {
    private Validator<User> userValidator;
    private UserDBPagingRepository userRepository;
    private FriendDataBaseRepository friendRepository;
    private RequestPagingRepository requestRepository;
    private ServiceMessageDB serviceMessageDB;
    private Node initial;

    private ServiceDbForFriendship serviceDbForFriendship;
    private ServiceDbForRequest serviceDbForRequest;

    ObservableList<User> model = FXCollections.observableArrayList();
    ObservableList<Message> modelMessage = FXCollections.observableArrayList();
    ObservableList<Friendship> modelFriendship = FXCollections.observableArrayList();
    private ServiceDbForUser service;
    private Stage primaryStage;

    @FXML
    public AnchorPane root;
    @FXML
    public BorderPane borderPane;
    @FXML
    public Button buttonMyRequests;
    @FXML
    public Button buttonMessages;
    @FXML
    public Button buttonFriends;
    @FXML
    public Button buttonUsers;
    @FXML
    public Button buttonWrite;
    @FXML
    public Button buttonMyFriends;
    @FXML
    public Text textUsername;
    private User u;


    public BorderPane getBorderPane() {
        return borderPane;
    }

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }
    public void setUserTaskService(BorderPane borderPane, Stage primaryStage, AnchorPane anchor, UserDBPagingRepository userRepository, FriendDataBaseRepository friendRepository, RequestPagingRepository requestRepository, ServiceDbForUser serviceForDatabase, ServiceDbForFriendship serviceDbForFriendship, ServiceDbForRequest serviceDbForRequest, ServiceMessageDB serviceMessageDB,  User u) {
        service = serviceForDatabase;
        this.u=u;
        this.root=anchor;
        this.borderPane=borderPane;
        this.primaryStage=primaryStage;
        this.serviceDbForFriendship=serviceDbForFriendship;
        this.serviceDbForRequest=serviceDbForRequest;
        this.serviceMessageDB=serviceMessageDB;
        this.userRepository=userRepository;
        this.friendRepository=friendRepository;
        this.requestRepository=requestRepository;
        this.textUsername.setText(String.valueOf(u.getFirstName().charAt(0)) + String.valueOf(u.getLastName().charAt(0)));
        service.addObserver(this);
        initModel();
    }
    public void clickUserName(ActionEvent ev){

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Sign out", "Connect with another count", "Back");
        // Deschide ComboBox când textul este apăsat
        //comboBox.show();
        comboBox.setOnAction(event -> {
            String selectedOption = comboBox.getSelectionModel().getSelectedItem();
            if(selectedOption.equals("Sign out"))
                System.out.println("Sign out");
            else if(selectedOption.equals("Connect with another count")) {
                try {
                    handleLoginWithAnotherCount(ev);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(selectedOption.equals("Back"))
                System.out.println("Back");
        });
        VBox root = new VBox(textUsername, comboBox);
        Scene scene = new Scene(root, 300, 200);
        Stage stage = new Stage();
        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        stage.show();
    }
    private void initModel() {
    }
    public void handleLoginWithAnotherCount(ActionEvent ev) throws IOException {
        FXMLLoader messageLoader = new FXMLLoader();
        messageLoader.setLocation(getClass().getResource("login.fxml"));
        AnchorPane messageTaskLayout = messageLoader.load();
        Stage stage=new Stage();
        stage.setScene(new Scene(messageTaskLayout));
        LoginController messageTaskController = messageLoader.getController();
        BorderPane pane=(BorderPane)messageTaskLayout.getChildren().get(0);
        messageTaskController.setUserTaskService(pane,stage,messageTaskLayout, userRepository,friendRepository,requestRepository,service,serviceDbForFriendship,serviceDbForRequest,serviceMessageDB);
        stage.show();
    }
    public void handleUsers(ActionEvent ev){
        try {
            showUsers();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void showUsers() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("users.fxml"));
        borderPane.setCenter(loader.load());
        UsersController editMessageViewController = loader.getController();
        editMessageViewController.setUserTaskService(borderPane,primaryStage,root,userRepository,friendRepository,requestRepository,service,serviceDbForFriendship,serviceDbForRequest,serviceMessageDB, u);
    }

    private void initModelMessage() {
        Iterable<Message> messages = serviceMessageDB.getAllMessages();
        List<Message> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        modelMessage.setAll(messageTaskList);
    }

    public void initializeTableData(){
        Iterable<User> allUsers = service.getAllUser();
        List<User> allUsersList = StreamSupport.stream(allUsers.spliterator(), false).toList();
        model.setAll(allUsersList);
    }



    public void handleAddMessage(ActionEvent ev) {
        showMessage(null);
    }
    public void showMessage(Message message) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("edit-message.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

             EditMessage editMessageViewController = loader.getController();

            editMessageViewController.setService(serviceMessageDB,service, dialogStage, message);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showFriends(User f){
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("friendship.fxml"));
            borderPane.setCenter(loader.load());
            primaryStage.setTitle("Edit Message");
            FriendshipController editMessageViewController = loader.getController();
            editMessageViewController.setService(userValidator,userRepository,friendRepository,requestRepository,service,serviceDbForFriendship,serviceDbForRequest,primaryStage, f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleMyFriends(ActionEvent e){
        showFriends(u);
    }
    public void handleFriendUser(ActionEvent ev){

            showMessageTaskEditDialogFriend(u);

    }

    private void showMessageTaskEditDialogFriend(User selected) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("edit-request-view.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Request");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            EditRequestController editUserViewController = loader.getController();
            editUserViewController.setService(serviceDbForFriendship,serviceDbForRequest, dialogStage, selected);
            dialogStage.show();

        } catch (IOException e) {

        }
    }
    public void handleFriendsRequest(ActionEvent ev){
            showRequest(u);
    }
    private void showRequest(User selected) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("requests.fxml"));
            borderPane.setCenter(loader.load());
            RequestController editUserViewController = loader.getController();
            editUserViewController.setUserTaskService(serviceDbForRequest,service,selected);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleMessagesForOneUser(ActionEvent ev){
            showMessages(u);
    }
    private void showMessages(User selected) {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("messages.fxml"));
            borderPane.setCenter(loader.load());
            MessageTaskController editUserViewController = loader.getController();
            editUserViewController.setUserTaskService(serviceMessageDB,service,serviceDbForFriendship,selected,root,modelMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {
        initModel();
    }
}
