package com.example.laborator7;

import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.*;
import com.example.laborator7.repository.FriendDataBaseRepository;
import com.example.laborator7.repository.FriendshipDBPagingRepository;
import com.example.laborator7.repository.RequestRepository;
import com.example.laborator7.repository.UserDataBaseRepository;
import com.example.laborator7.service.ServiceDbForRequest;
import com.example.laborator7.service.ServiceForMessage;
import com.example.laborator7.service.serviceForPerson.ServiceDbForFriendship;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.service.serviceForPerson.ServiceForDatabase;
import com.example.laborator7.utils.events.request.RequestChangeEvent;
import com.example.laborator7.utils.events.user.UserTaskChangeEvent;
import com.example.laborator7.utils.observer.Observer;
import com.example.laborator7.validator.FriendshipValidator;
import com.example.laborator7.validator.UserValidator;
import com.example.laborator7.validator.Validator;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

public class RequestController implements Observer<RequestChangeEvent> {

    private ServiceDbForUser serviceForDatabase;

    private ServiceDbForRequest serviceDbForRequest;
    ObservableList<RequestFriendship> model = FXCollections.observableArrayList();
    ObservableList<Message> modelMessage = FXCollections.observableArrayList();
    private ServiceDbForUser service;
    private String numberPg;

    @FXML
    public TableView<RequestFriendship> userTableView;

    @FXML
    TableColumn<RequestFriendship,Long> IDField;
    @FXML
    TableColumn<RequestFriendship,String> id1Field;
    @FXML
    TableColumn<RequestFriendship,String> id2Field;
    @FXML
    TableColumn<RequestFriendship, LocalDateTime> dateTimeTableColumn;
    @FXML
    TableColumn<RequestFriendship,String> status;
    @FXML
    TextField textFiledNrPg;
    private User u;

    public void setU(User u) {
        this.u = u;
    }

    @FXML
    public void initialize() {
        id1Field.setCellValueFactory(cellData -> {
            RequestFriendship message = cellData.getValue();
        Long fromUser = message.getId1();
        User u1=serviceForDatabase.findUser(fromUser);
            if (u1 != null) {
                return new SimpleObjectProperty<>(u1.getFirstName());
            } else {
                return new SimpleObjectProperty<>("");
            }

        }
        );
        id2Field.setCellValueFactory(cellData -> {
                    RequestFriendship message = cellData.getValue();
                    Long fromUser = message.getId2();
                    User u2=serviceForDatabase.findUser(fromUser);
                    if (u2 != null) {
                        return new SimpleObjectProperty<>(u2.getFirstName());
                    } else {
                        return new SimpleObjectProperty<>("");
                    }

                }
        );
        IDField.setCellValueFactory(new PropertyValueFactory<RequestFriendship, Long>("id"));
        dateTimeTableColumn.setCellValueFactory(new PropertyValueFactory<RequestFriendship, LocalDateTime>("date"));
        status.setCellValueFactory(new PropertyValueFactory<RequestFriendship, String>("status"));
        userTableView.setItems(model);
        listenerForPg();
    }

    public void listenerForPg(){
        textFiledNrPg.textProperty().addListener(o->{
             numberPg=textFiledNrPg.getText();
            initModel();
        });
    }
    public void setUserTaskService(ServiceDbForRequest messageTaskService, ServiceDbForUser serviceForDatabase,User user) {

        setU(user);
        this.serviceForDatabase=serviceForDatabase;
        serviceDbForRequest = messageTaskService;
        serviceDbForRequest.addObserver(this);
        initModel();
    }


    private void initModel() {
        if(numberPg==null){
            numberPg="10";
        }
        Iterable<RequestFriendship> messages = serviceDbForRequest.getAllRequestOnPage(1,u.getId(), Integer.valueOf(numberPg));
        List<RequestFriendship> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }
    public void handleNextPage(ActionEvent ev){
        Iterable<RequestFriendship> messages = serviceDbForRequest.getNextMessage(u.getId());
        List<RequestFriendship> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }
    public void handlePrevious(ActionEvent ev){
        Iterable<RequestFriendship> messages = serviceDbForRequest.getPreviousMessage(u.getId());
        List<RequestFriendship> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }

    public void initializeTableData(User u){
        Iterable<RequestFriendship> allUsers = serviceDbForRequest.getAllRequest(u.getId());
        List<RequestFriendship> allUsersList = StreamSupport.stream(allUsers.spliterator(), false).toList();
        model.setAll(allUsersList);
    }
    public void handleDeleteUser(ActionEvent actionEvent){
      RequestFriendship toBeDeleted= userTableView.getSelectionModel().getSelectedItem();//setregem care e selecatat
        if(toBeDeleted==null){
            UserAlert.showErrorMessage(null,"Please select before hitting delete");
        }
        else{
            toBeDeleted=serviceDbForRequest.deleteServiceUser(toBeDeleted);
            if(toBeDeleted==null || toBeDeleted.getId()==null){
                //failed
                UserAlert.showMessage(null, Alert.AlertType.WARNING,"Delete failed","please try again");
            }
            else{
                //succes
                UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"rejected","rejected");
            }
        }
    }
    public void handleUpdateUser(ActionEvent ev) {
        RequestFriendship selected = userTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            serviceDbForRequest.updateServiceUser(selected);

        } else
            UserAlert.showErrorMessage(null, "NU ati selectat nici un student");
    }

    @Override
    public void update(RequestChangeEvent userTaskChangeEvent) {
        initModel();
    }
}
