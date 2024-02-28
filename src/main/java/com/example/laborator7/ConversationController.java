package com.example.laborator7;
import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.ReplyMessage;
import com.example.laborator7.domain.User;
import com.example.laborator7.service.serviceForPerson.ServiceDbForUser;
import com.example.laborator7.service.serviceForPerson.ServiceMessageDB;
import com.example.laborator7.utils.events.MessageTaskChangeEvent;
import com.example.laborator7.utils.observer.Observer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConversationController implements Observer<MessageTaskChangeEvent> {
    @FXML
    private VBox vBox;
    @FXML
    private TextField textFieldMessage;
    @FXML
    private Button sendButton;
    private User loogedUser;
    private User who;
    public Message getM() {
        return m;
    }

    public void setM(Message m) {
        this.m = m;
    }

    public ReplyMessage getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(ReplyMessage replyMessage) {
        this.replyMessage = replyMessage;
    }

    @FXML
    private ScrollPane scrollPane;
    private ServiceMessageDB serviceMessageDB;
    private ServiceDbForUser serviceDbForUser;
    private Message m;
    private User u;
    private ReplyMessage replyMessage;
    ObservableList<Message> model = FXCollections.observableArrayList();

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }

    public ObservableList<Message> getModel() {
        return model;
    }

    public void setModel(ObservableList<Message> model) {
        this.model = model;
    }

    public int page =0;
    public void handleSend(){
        vBox.heightProperty().addListener(new ChangeListener<Number>() {
                        @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scrollPane.setVvalue((Double) newValue);
            }
        });
        String name=textFieldMessage.getText();
        List<User>userList=new ArrayList<>();
        User to;
        //n=m.getId();
        if(u.getId()==m.getFrom().getId()){
           Long  toU=serviceMessageDB.findTo(m.getId());
           to=serviceDbForUser.findUser(toU);
        }
        else{
            to=m.getFrom();
        }
        userList.add(to);
        //m.setId();
        ReplyMessage replyMessage1=new ReplyMessage(u,userList,null,name,m);
        serviceMessageDB.addMessage(replyMessage1);
        textFieldMessage.clear();
    }
 public  void addLabel(Message messageFromAUser, VBox vB){
        HBox hBox=new HBox();
        if(messageFromAUser.getFrom().getId()==u.getId()) {
            hBox.setAlignment(Pos.CENTER_RIGHT);
        }
        if(messageFromAUser.getFrom().getId()!=u.getId()) {
            hBox.setAlignment(Pos.CENTER_LEFT);
        }
        hBox.setPadding(new Insets(5,15,5,10));
        Text text = new Text(messageFromAUser.getMessage());
        TextFlow textFlow = new TextFlow(text);//il imparte si pe linia urmatoare daca e pea mare pt box-ul nostru
        textFlow.setStyle("-fx-background-color:rgb(233,233,235)"+";-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5,10,5,10));
        hBox.getChildren().add(textFlow);
        vB.getChildren().add(hBox);
        //update(null);
    }
    private void addLabelAtBeginning(Message messageFromAUser, VBox vB) {
        HBox hBox = new HBox();
        if (messageFromAUser.getFrom().getId() == u.getId()) {
            hBox.setAlignment(Pos.CENTER_RIGHT);
        }
        if (messageFromAUser.getFrom().getId() != u.getId()) {
            hBox.setAlignment(Pos.CENTER_LEFT);
        }
        hBox.setPadding(new Insets(5, 15, 5, 10));
        Text text = new Text(messageFromAUser.getMessage());
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color:rgb(233,233,235)" + ";-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);

        // Adăugați la începutul listei de copii
        vB.getChildren().add(0, hBox);
    }

//    @Override
//    public void update(MessageTaskChangeEvent messageTaskChange) {
//        if(messageTaskChange.getType()== ChangeMessageType.RECEIVED){
//            addLabel(replyMessage.getOldMessage(), vBox);
//        }
//        if(messageTaskChange.getType()== ChangeMessageType.SENT){
//            initialize();
//        }
//
//    }
private boolean loadingMessages = false;

    @FXML
    public void initialize() {
        scrollPane.setVvalue(1.0);
        scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double scrollValue = newValue.doubleValue();
                if (scrollValue == 0.0 && !loadingMessages) {
                    // Utilizatorul a derulat în partea de sus
                    loadingMessages = true;
                    loadMoreMessages();
                }
            }
        });
    }

    private void loadMoreMessages() {
        try {
            // Obține mesajele adiționale din serviciu sau altă sursă
            List<Message> additionalMessages = getAdditionalMessages();
            Collections.sort(additionalMessages, Comparator.comparing(Message::getId).reversed());
            //scrollPane.setVvalue(1.0);
            // Adaugă mesajele în VBox
            for (Message message : additionalMessages) {
                addLabelAtBeginning(message, vBox);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Gestionarea erorilor corespunzător
        } finally {
            loadingMessages = false;
        }
    }

    private List<Message> getAdditionalMessages() {
        Long id_from=u.getId();
        Long id_to;
        if(id_from==m.getFrom().getId()){
            id_to=serviceMessageDB.findTo(m.getId());
        }
        else{
            id_to=m.getFrom().getId();
        }
        Set<Message>mess=serviceMessageDB.getNextConv(id_from,id_to);
        if(page!=0) {
            scrollPane.setVvalue(1.0- (double) 1 / page);
        }
        //initialize();
        List<Message> messageTaskList = StreamSupport.stream(mess.spliterator(), false)
                .collect(Collectors.toList());
        Collections.sort(messageTaskList, Comparator.comparing(Message::getId).reversed());
        //scrollPane.setVvalue(0.5);
        return messageTaskList ;// În exemplu, întoarcem o listă goală
    }
    public void setUserTaskService(ServiceMessageDB serviceMessageDB,ServiceDbForUser serviceDbForUser, Message selected,User u ,AnchorPane root,ObservableList<Message> model) {
        setM(selected);
        setU(u);
        setModel(model);
        this.serviceDbForUser=serviceDbForUser;
        this.serviceMessageDB=serviceMessageDB;
        serviceMessageDB.addObserver(this);
        initModel(selected,u);
    }

    @Override
    public void update(MessageTaskChangeEvent messageTaskChangeEvent) {
        initModel(m,u);
    }

    private void initModel(Message selected,User u) {
        Long id_from=u.getId();
        Long id_to;
        if(id_from==selected.getFrom().getId()){
            id_to=serviceMessageDB.findTo(selected.getId());
        }
        else{
            id_to=selected.getFrom().getId();
        }

        Iterable<Message>messages=serviceMessageDB.getConversation(1,id_from,id_to);
        List<Message> messageTaskList = StreamSupport.stream(messages.spliterator(), false).sorted(Comparator.comparing(Message::getId))
                .collect(Collectors.toList());
        vBox.getChildren().clear();
        messageTaskList.forEach(mes-> {

                    addLabel(mes, vBox);

                });
        model.addAll(0, messageTaskList);

        //model.setAll(messageTaskList);
    }
}
