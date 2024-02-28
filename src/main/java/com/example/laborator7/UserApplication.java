package com.example.laborator7;

import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.*;
import com.example.laborator7.repository.paging.PagingRepository;
import com.example.laborator7.service.ServiceDbForRequest;
import com.example.laborator7.service.serviceForPerson.ServiceMessageDB;
import com.example.laborator7.service.serviceForPerson.ServiceDbForFriendship;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.validator.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UserApplication extends Application {
    private Validator<User> userValidator;
    private UserDBPagingRepository userRepository;
    private FriendshipDBPagingRepository friendRepository;
    private RequestPagingRepository requestRepository;
    private ServiceDbForUser serviceForDatabase;
    private MessageDBPagingRepository messageRepository;
    private ServiceMessageDB serviceMessageDB;
    private ServiceDbForFriendship serviceDbForFriendship;
    private ServiceDbForRequest serviceDbForRequest;
    private PagingRepository<Long,User> userRepositoryPaging;
    private ServiceDbForUser serviceDbForUser;
    public static void main(String[] args) {
        System.out.println("ok");
        launch();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("ok");
        try {
            Validator<User> validUser = new UserValidator();


            Validator<Friendship> validFriend = new FriendshipValidator();
            String url = "jdbc:postgresql://localhost:5432/socialnetwork";
            String username = "postgres";
            String password = "liana23062003";
            String users = "users";
            String friendships = "friendships";
            userRepository = new UserDBPagingRepository(url, username, password, users, validUser);
            friendRepository = new FriendshipDBPagingRepository(url, username, password, friendships, validFriend, userRepository);
            requestRepository=new RequestPagingRepository(url,username,password,friendships,validFriend,userRepository,friendRepository);
            serviceForDatabase = new ServiceDbForUser(userRepository,friendRepository);
            serviceDbForRequest=new ServiceDbForRequest(userRepository,requestRepository,friendRepository);
            serviceDbForFriendship=new ServiceDbForFriendship(userRepository,friendRepository);
            Validator<Message> messageValidator = new MessageValidator();
            messageRepository=new MessageDBPagingRepository(url,username,password,users,messageValidator,userRepository);
             serviceMessageDB = new ServiceMessageDB(messageRepository, userRepository);
            FXMLLoader messageLoader = new FXMLLoader();
            messageLoader.setLocation(getClass().getResource("login.fxml"));
            AnchorPane messageTaskLayout = messageLoader.load();
            primaryStage.setScene(new Scene(messageTaskLayout));
            LoginController messageTaskController = messageLoader.getController();
            BorderPane pane=(BorderPane)messageTaskLayout.getChildren().get(0);
            messageTaskController.setUserTaskService(pane,primaryStage,messageTaskLayout, userRepository,friendRepository,requestRepository,serviceForDatabase,serviceDbForFriendship,serviceDbForRequest,serviceMessageDB);

            primaryStage.setTitle("Hello!");
            //primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Rethrow the exception to ensure it's not silently ignored
        }
    }
}
