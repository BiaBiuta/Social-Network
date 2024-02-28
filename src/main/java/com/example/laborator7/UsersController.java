package com.example.laborator7;

import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.Entity;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UsersController implements Observer<UserTaskChangeEvent> {
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
    @FXML
    public Button nextButton;
    @FXML
    public Button previousButton;
    @FXML
    public Button buttonAdd;
    @FXML
    public Button buttonDelete;
    @FXML
    public Button buttonUpdate;

    @FXML
    public TableView<User> userTableView;
   public User u;
    @FXML
    public AnchorPane root;
    @FXML
    public BorderPane borderPane;
    @FXML
    public Stage primaryStage;
    @FXML
    TableColumn<User,String> firstNameField;
    @FXML
    TableColumn<User,String> lastNameField;
    @FXML
    TableColumn<User,Integer> idField;
    @FXML
    TextField textFieldNumberPg;
    public String numberPg;

    @FXML
    public void initialize() {

        firstNameField.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameField.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        idField.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        userTableView.setItems(model);
        listenerForPg();
    }
    public void setUserTaskService(BorderPane borderPane, Stage primaryStage, AnchorPane anchor, UserDBPagingRepository userRepository, FriendDataBaseRepository friendRepository, RequestPagingRepository requestRepository, ServiceDbForUser serviceForDatabase, ServiceDbForFriendship serviceDbForFriendship, ServiceDbForRequest serviceDbForRequest, ServiceMessageDB serviceMessageDB, User u) {
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

        //setInitial(borderPane.getCenter());
        service.addObserver(this);
        initModel();


    }
    public void listenerForPg() {
        textFieldNumberPg.textProperty().addListener(o -> {
            numberPg = textFieldNumberPg.getText();
            initModel();
        });
    }
    private void initModel() {
        String numberPg=textFieldNumberPg.getText();
        if(numberPg.equals("")) {
            numberPg = "5";
        }
        int numberInt=Integer.parseInt(numberPg);
        Iterable<User> messages = service.getAllUserOnPage(1,numberInt,u.getId());
        List<User> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }
    public void handleDeleteUser(ActionEvent actionEvent){
        Entity<Long> toBeDeleted= userTableView.getSelectionModel().getSelectedItem();//setregem care e selecatat
        if(toBeDeleted==null){
            UserAlert.showErrorMessage(null,"Please select before hitting delete");
        }
        else{
            Long oldId=toBeDeleted.getId();
            toBeDeleted=service.deleteServiceUser((toBeDeleted.getId()));
            if(toBeDeleted==null || toBeDeleted.getId()==null){
                //failed
                UserAlert.showMessage(null, Alert.AlertType.WARNING,"Delete failed","please try again");
            }
            else{
                //succes
                UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"s-a sters","yay");
            }
        }
    }
    public void handleAddUser(ActionEvent ev) {

        showMessageTaskEditDialog(null);
    }
    public void showMessageTaskEditDialog(User user) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("edit-user-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController editUserViewController = loader.getController();
            editUserViewController.setService(service, dialogStage, user,null);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public TableView<User> getUserTableView() {
        return userTableView;
    }

    public void setUserTableView(TableView<User> userTableView) {
        this.userTableView = userTableView;
    }
    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {

    }
    public void handleUpdateUser(ActionEvent ev) {
        User selected = userTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showMessageTaskEditDialog(selected);
        } else
            UserAlert.showErrorMessage(null, "NU ati selectat nici un student");
    }
    public void handleNextPageUser(ActionEvent ev){
        Iterable<User> messages = service.getNextUser(u.getId());
        List<User> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }
    public void handlePreviousPageUser(ActionEvent ev){
        Iterable<User> messages = service.getPreviousUser(u.getId());
        List<User> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }
}
