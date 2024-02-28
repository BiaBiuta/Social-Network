package com.example.laborator7;

import java.util.Objects;
import java.util.stream.Collectors;
import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.DTO;
import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.FriendDataBaseRepository;
import com.example.laborator7.repository.RequestRepository;
import com.example.laborator7.repository.UserDataBaseRepository;
import com.example.laborator7.service.ServiceDbForRequest;
import com.example.laborator7.service.serviceForPerson.ServiceMessageDB;
import com.example.laborator7.service.serviceForPerson.ServiceDbForFriendship;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.utils.events.MessageTaskChangeEvent;
import com.example.laborator7.utils.observer.Observer;
import com.example.laborator7.validator.Validator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.StreamSupport;

public class MessageTaskController implements Observer<MessageTaskChangeEvent> {



        private Validator<User> userValidator;

        private UserDataBaseRepository userRepository;
        private FriendDataBaseRepository friendRepository;
        private RequestRepository requestRepository;
        private ServiceDbForUser serviceForDatabase;
        private ServiceDbForFriendship serviceDbForFriendship;
        private ServiceDbForRequest serviceDbForRequest;
        private ServiceMessageDB serviceMessageDB;
        ObservableList<Message> model = FXCollections.observableArrayList();
        ObservableList<Message> modelMessage = FXCollections.observableArrayList();
        private ServiceDbForUser service;
        private String numberPg;
        @FXML
        public TextField textFieldNumberPg;
        @FXML
        public TableView<Message> userTableView;

//        @FXML
//        TableColumn<Message,Long> IDField;
        @FXML
        TableColumn<Message,String> fromField;
        @FXML
        TableColumn<Message, String> toField;
        @FXML
        TableColumn<Message, LocalDateTime> dateTimeTableColumn;
        @FXML
        TableColumn<Message,String> status;
        @FXML
        AnchorPane additionalAnchorPane;
        private ObservableList<Message>modelul;
        @FXML
        AnchorPane mainAnchorPane;
        private User u;
        private Message m;

        public void setU(User u) {
            this.u = u;
        }

@FXML
    public void initialize() {

            fromField.setCellValueFactory(cellData -> {
                Message message = cellData.getValue();

                User fromUser = message.getFrom();
                if (fromUser != null) {

                    return new SimpleStringProperty(fromUser.getFirstName() + " " + fromUser.getLastName());
                } else {
                    return new SimpleStringProperty("");
                }

            });
            toField.setCellValueFactory(cellData -> {
                Message message = cellData.getValue();

                User fromUser = message.getFrom();

                if(Objects.equals(fromUser.getId(), u.getId()))
                {
                    Long to=serviceMessageDB.findTo(message.getId());

                    User toU=serviceForDatabase.findUser(to);
                    if (toU != null) {
                        return new SimpleStringProperty(toU.getFirstName() + " " + toU.getLastName());
                    } else {
                        return new SimpleStringProperty("");
                    }
                }
                else{
                    if (fromUser != null) {

                        return new SimpleStringProperty(u.getFirstName() + " " + u.getLastName());
                    } else {
                        return new SimpleStringProperty("");
                    }
                }


            });
            dateTimeTableColumn.setCellValueFactory(new PropertyValueFactory<Message, LocalDateTime>("date"));
            status.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));
            userTableView.setItems(model);
        listenerForPg();
        }
    public  void listenerForPg() {
        textFieldNumberPg.textProperty().addListener(o -> {
            numberPg = textFieldNumberPg.getText();
            initModel();
        });
    }
    public ObservableList<Message> getModelul() {
        return modelul;
    }

    public void setModelul(ObservableList<Message> modelul) {
        this.modelul = modelul;
    }

    public void setUserTaskService(ServiceMessageDB messageTaskService,ServiceDbForUser serviceUser,ServiceDbForFriendship serviceDbForFriendship, User user, AnchorPane mainAnchorPane, ObservableList<Message>modelul) {
            setModelul(modelul);
            setU(user);
            this.mainAnchorPane=mainAnchorPane;
            serviceMessageDB = messageTaskService;
            this.serviceForDatabase=serviceUser;
            this.serviceDbForFriendship=serviceDbForFriendship;
            serviceMessageDB.addObserver(this);
            initialize();
            initModel();
        }


        private void initModel() {
            String numberPg=textFieldNumberPg.getText();
            if(Objects.equals(numberPg, "")){
                numberPg="10";
            }
            int number = Integer.parseInt(numberPg);
            Iterable<Message> messages = serviceMessageDB.getAllMessageOnPage(1,u.getId(), number);
            List<Message> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                    .collect(Collectors.toList());
            model.setAll(messageTaskList);
        }
        public void handleNextPage(ActionEvent ev){
            Iterable<Message> messages = serviceMessageDB.getNextMessage(u.getId());
            List<Message> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                    .collect(Collectors.toList());
            model.setAll(messageTaskList);
        }
        public void handlePrevious(ActionEvent ev){
            Iterable<Message> messages = serviceMessageDB.getPreviousMessage(u.getId());
            List<Message> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                    .collect(Collectors.toList());
            model.setAll(messageTaskList);
        }
    public void handleOpenConversation(ActionEvent ev){
        Message selected = userTableView.getSelectionModel().getSelectedItem();
        showConversationWindow(selected);
    }
    public void showConversationWindow(Message selected){

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("conversatie.fxml"));
        AnchorPane root = null;
        try {
            root = (AnchorPane) loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage dialogStage = new Stage();
        dialogStage.setTitle(" Messages");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root));


        ConversationController editUserViewController = loader.getController();
        editUserViewController.setUserTaskService(serviceMessageDB,serviceForDatabase,selected,u,root,modelMessage);

        dialogStage.show();

    }
        public void handleDeleteUser(ActionEvent actionEvent){
            Message toBeDeleted= userTableView.getSelectionModel().getSelectedItem();//setregem care e selecatat
            if(toBeDeleted==null){
                UserAlert.showErrorMessage(null,"Please select before hitting delete");
            }
            else{
                toBeDeleted=serviceMessageDB.updateMessage(toBeDeleted);
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
            Message selected = userTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                serviceMessageDB.updateMessage(selected);
                //serviceDbForRequest.deleteService(selected.getId());
            } else
                UserAlert.showErrorMessage(null, "NU ati selectat nici un student");
        }



    @Override
    public void update(MessageTaskChangeEvent messageTaskChangeEvent) {
        initModel();
    }
}


