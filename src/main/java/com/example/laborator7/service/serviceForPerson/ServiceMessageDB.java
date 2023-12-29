package com.example.laborator7.service.serviceForMessage;


import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.MessageDataBaseRepository;
import com.example.laborator7.repository.UserDataBaseRepository;
import com.example.laborator7.service.ServiceForMessage;
import com.example.laborator7.utils.events.ChangeMessageType;
import com.example.laborator7.utils.events.MessageTaskChangeEvent;
import com.example.laborator7.utils.events.user.ChangeEventType;
import com.example.laborator7.utils.events.user.UserTaskChangeEvent;
import com.example.laborator7.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceMessageDB implements ServiceForMessage {
    private final MessageDataBaseRepository repositoryMessage;
    private final UserDataBaseRepository repositoryUser;
    private List<Observer<MessageTaskChangeEvent>> observers=new ArrayList<>();
    public ServiceMessageDB(MessageDataBaseRepository repositoryMessage, UserDataBaseRepository repositoryUser) {
        this.repositoryMessage = repositoryMessage;
        this.repositoryUser = repositoryUser;
    }

    @Override
    public Message addMessage(Message m) {
        try{
            User n=m.getFrom();
            User fromUser=repositoryUser.findOneName(n.getFirstName()+" "+n.getLastName());

            Message message1=new Message(fromUser,m.getAll(),m.getDate(),m.getMessage());
             repositoryMessage.save(message1);
            if(message1!=null){
                notifyObservers(new MessageTaskChangeEvent(ChangeMessageType.SENT,message1));
            }
            return message1;
        }catch (Exception e){
            return null;
        }
    }



    @Override
    public Message deleteMessage(Long id) {
        return null;
    }

    @Override
    public Message updateMessage(Message m) {
        return null;
    }

    @Override
    public Iterable<Message> getAllMessages() {
        return null;
    }


    public Iterable<Message> getAllMessages(Long id) {
        return repositoryMessage.findAll();
    }


    @Override
    public void addObserver(Observer<MessageTaskChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageTaskChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageTaskChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
