package com.example.laborator7.controllerGui;

import com.example.laborator7.controllerGui.allert.UserAlert;
import com.example.laborator7.domain.Entity;
import com.example.laborator7.domain.User;
import com.example.laborator7.service.ServiceDbForUser;
import com.example.laborator7.service.ServiceForDatabase;
import com.example.laborator7.utils.events.UserTaskChangeEvent;
import com.example.laborator7.utils.observer.Observer;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserTaskChangeEvent> {
    ObservableList<User> model = FXCollections.observableArrayList();
    private ServiceDbForUser service;
    @FXML
    public TableView<User> userTableView;
    @FXML
    TableColumn<User,String> firstNameField;
    @FXML
    TableColumn<User,String> lastNameField;
    @FXML
    TableColumn<User,Integer> idField;
    @FXML
    private void initialize() {
        firstNameField.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameField.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        idField.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        userTableView.setItems(model);

    }


    public void setUserTaskService(ServiceDbForUser messageTaskService) {
        service = messageTaskService;
        service.addObserver(this);
        initModel();
    }

    private void initModel() {
        Iterable<User> messages = service.getAllUser();
        List<User> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }

    public void initializeTableData(){
        Iterable<User> allUsers = service.getAllUser();
        List<User> allUsersList = StreamSupport.stream(allUsers.spliterator(), false).toList();
        model.setAll(allUsersList);
    }
    public void handleDeleteUser(ActionEvent actionEvent){
        Entity<Long> toBeDeleted= userTableView.getSelectionModel().getSelectedItem();//setregem care e selecatat
        if(toBeDeleted==null){
            UserAlert.showErrorMessage(null,"Please select before hitting delete");
        }
        else{
            Long oldId=toBeDeleted.getId();
            toBeDeleted=service.deleteServiceUser((toBeDeleted.getId()));
            if(toBeDeleted==null || toBeDeleted.getId()!=null){
                //failed
                UserAlert.showMessage(null, Alert.AlertType.WARNING,"Delete failed","please try again");
            }
            else{
                //delete succes
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
            editUserViewController.setService(service, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleUpdateUser(ActionEvent ev) {
        User selected = userTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showMessageTaskEditDialog(selected);
        } else
            UserAlert.showErrorMessage(null, "NU ati selectat nici un student");
    }

    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {
        initModel();
    }
}
