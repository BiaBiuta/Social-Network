package com.example.laborator7;

import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.RequestFriendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.FriendDataBaseRepository;
import com.example.laborator7.repository.RequestRepository;
import com.example.laborator7.repository.UserDataBaseRepository;
import com.example.laborator7.repository.paging.PagingRepository;
import com.example.laborator7.service.ServiceDbForRequest;
import com.example.laborator7.service.serviceForPerson.ServiceDbForFriendship;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.service.serviceForPerson.ServiceMessageDB;
import com.example.laborator7.utils.events.friendship.FriendTaskChangeEvent;
import com.example.laborator7.utils.observer.Observable;
import com.example.laborator7.utils.observer.Observer;
import com.example.laborator7.validator.Validator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipController implements Observer<FriendTaskChangeEvent> {
    private Validator<User> userValidator;
    private PagingRepository userRepository;
    private FriendDataBaseRepository friendRepository;
    private RequestRepository requestRepository;

    private ServiceDbForUser serviceForDatabase;
    private ServiceDbForFriendship serviceDbForFriendship;
    private ServiceDbForRequest serviceDbForRequest;
   // ObservableList<User> model = FXCollections.observableArrayList();
    ObservableList<Friendship> modelFriendship = FXCollections.observableArrayList();
    private ServiceDbForUser service;
    @FXML
    public TableView<Friendship> userTableView;

//    @FXML
//    TableColumn<Friendship,Long> IDField;
    @FXML
    TableColumn<Friendship,String> id1Field;
    @FXML
    TableColumn<Friendship,String> id2Field;
    @FXML
    TableColumn<Friendship, LocalDateTime> dateTimeTableColumn;
    @FXML
    TextField textFieldNumberPg;
    Stage dialogStage;
    private User u;
    private Stage primaryStage;
    private String numberPg;

    public void setU(User u) {
        this.u = u;
    }
    private void setTableAllFriends()
    {

        id1Field.setCellValueFactory(cellData->{
            Friendship friend=cellData.getValue();
            Long fromUser=friend.getUserId1();
            Long toUser=friend.getUserId2();
            User u1=serviceForDatabase.findUser(fromUser);
            String name=u1.getFirstName()+" "+u1.getLastName();

            return new SimpleObjectProperty<>(name);
        });
        id2Field.setCellValueFactory(cellData->{
            Friendship friend=cellData.getValue();
            Long toUser=friend.getUserId2();
            User u2=serviceForDatabase.findUser(toUser);
            String name2=u2.getFirstName()+" "+u2.getLastName();
            return new SimpleObjectProperty<>(name2);
        });
        dateTimeTableColumn.setCellValueFactory(new PropertyValueFactory<Friendship, LocalDateTime>("date"));

    }
    @FXML
    public void initialize() {

        setTableAllFriends();
        userTableView.setItems(modelFriendship);
        listenerForPg();
    }
    public void setService(Validator<User> userValidator,
    PagingRepository userRepository,
    FriendDataBaseRepository friendRepository,
    RequestRepository requestRepository,
    ServiceDbForUser serviceForDatabase,
    ServiceDbForFriendship serviceDbForFriendship,
    ServiceDbForRequest serviceDbForRequest, Stage stage,User u) {
        setU(u);
        this.primaryStage=stage;
        this.serviceDbForFriendship = serviceDbForFriendship;
        this.userRepository= userRepository;
        this.friendRepository=friendRepository;
        this.requestRepository=requestRepository;
        this.serviceForDatabase=serviceForDatabase;
        this.serviceDbForRequest=serviceDbForRequest;
        serviceDbForFriendship.addObserver(this);
        initModel();
//        }
    }
    private void initModel() {
        String numberPg=textFieldNumberPg.getText();
        if(numberPg.equals("")) {
            numberPg = "5";
        }
        int number=Integer.parseInt(numberPg);
        Iterable<Friendship> messages = serviceDbForFriendship.getAllFriendship(1,u.getId(),number);
        List<Friendship> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        modelFriendship.setAll(messageTaskList);
        userTableView.setItems(modelFriendship);
    }
    public void handleNextPage(ActionEvent ev){
        Iterable<Friendship> friendships = serviceDbForFriendship.getNextFriendship(u.getId());
        List<Friendship> messageTaskList = StreamSupport.stream(friendships.spliterator(), false)
                .collect(Collectors.toList());
        modelFriendship.setAll(messageTaskList);
    }
    public void handlePrevious(ActionEvent ev){
        Iterable<Friendship> messages = serviceDbForFriendship.getPreviousFriendship(u.getId());
        List<Friendship> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        modelFriendship.setAll(messageTaskList);
    }
    public void listenerForPg() {
        textFieldNumberPg.textProperty().addListener(o -> {
            numberPg = textFieldNumberPg.getText();
            initModel();
        });
    }
    @Override
    public void update(FriendTaskChangeEvent friendTaskChangeEvent) {

    }
}
