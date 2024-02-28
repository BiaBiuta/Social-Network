package com.example.laborator7.service.serviceForPerson;


import com.example.laborator7.domain.DTO;
import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.ReplyMessage;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.*;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.PageableImplementation;
import com.example.laborator7.service.ServiceForMessage;
import com.example.laborator7.utils.events.ChangeMessageType;
import com.example.laborator7.utils.events.MessageTaskChangeEvent;
import com.example.laborator7.utils.events.user.ChangeEventType;
import com.example.laborator7.utils.events.user.UserTaskChangeEvent;
import com.example.laborator7.utils.observer.Observer;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceMessageDB implements ServiceForMessage {
    private final MessageDBPagingRepository repositoryMessage;

    private final UserDBPagingRepository repositoryUser;
    private List<Observer<MessageTaskChangeEvent>> observers=new ArrayList<>();
    private int page=0;
    private int size = 3;

    public ServiceMessageDB(MessageDBPagingRepository repositoryMessage, UserDBPagingRepository repositoryUser) {
        this.repositoryMessage = repositoryMessage;
        this.repositoryUser = repositoryUser;

    }

    @Override
    public Message addMessage(Message m) {
        try{
            User n=m.getFrom();
            User fromUser=repositoryUser.findOneName(n.getFirstName()+" "+n.getLastName());
//            for(User a :m.getAll()){
////                ReplyMessage replyMessage=new ReplyMessage();
////                replyMessageRepository.save(replyMessage);
//                //notifyObservers( MessageTaskChangeEvent(ChangeMessageType.RECEIVED,replyMessage.getOldMessage()));
//            }
            Message message1=null;
            for(User a :m.getAll()) {
                message1=new Message(fromUser,m.getAll(),m.getDate(),m.getMessage());
                repositoryMessage.saveM(message1, a, 0L);

            }
            notifyObservers(new MessageTaskChangeEvent(ChangeMessageType.SENT,message1));
            return message1;
        }catch (Exception e){
            return null;
        }
    }
    public ReplyMessage addReply(ReplyMessage replyMessage){
        User n=replyMessage.getFrom();
        User fromUser=repositoryUser.findOneName(n.getFirstName()+" "+n.getLastName());
        Long id_message= replyMessage.getId();
        Message message1=null;
        for(User a :replyMessage.getAll()) {
            message1=new Message(fromUser,replyMessage.getAll(),replyMessage.getDate(),replyMessage.getMessage());
            repositoryMessage.saveM(message1, a,id_message);

        }
        notifyObservers(new MessageTaskChangeEvent(ChangeMessageType.RECEIVED,message1));
        return replyMessage;
    }


//    public Iterable <DTO>getAll(Long id){
//        return repositoryMessage.findAll1(id);
//    }
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
        //ArrayList<Message> l =  (ArrayList) replyMessageRepository.findAll();
       return null;
    }



    public Iterable<Message> getAllMessages(Long id) {
        Iterable<Message> messages = repositoryMessage.findAll(id);
        List<Message> messageList = new ArrayList<>(); ;
        messages.forEach(messageList::add);
        messageList.sort(Comparator.comparing(Message::getId));
        //notifyObservers(new MessageTaskChangeEvent(ChangeMessageType.RECEIVED));
        return messageList;
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

        observers.forEach(x->{
            x.update(t);});
    }
    public Set<Message> getPreviousMessage(Long id){
        this.page=page-1;
        return getAllMessageOnPage(this.page,id,size);
    }
    public Set<Message> getNextMessage(Long id){
        this.page=page+1;
        return getAllMessageOnPage(this.page,id,size);
    }
    public Set<Message> getNextConv(Long from,Long to){
        this.page=page+1;
        return getConversation(this.page,from,to);
    }
    public Set<Message> getPreviousConv(Long from,Long to){
        this.page=page-1;
        return getConversation(this.page,from,to);
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    public Set<Message> getConversation(int page,Long from,Long to){
            this.page=page;
            setSize(8);
            Pageable pageable = new PageableImplementation(page, this.size);
            Page<Message>messagePage=repositoryMessage.findAllPageable(pageable,from,to);
            return messagePage.getContent().collect(Collectors.toSet());
    }

    public Set<Message> getAllMessageOnPage(int page,Long id,Integer numberPg) {
        this.page=page;

            setSize(numberPg);

        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Message> messagePage=repositoryMessage.findAll(pageable,id);
        return messagePage.getContent().collect(Collectors.toSet());
    }

    public void sendMessage(Message message) {

        repositoryMessage.save(message);
        notifyObservers(new MessageTaskChangeEvent(ChangeMessageType.SENT,message));

    }
    public Set<Message> getAll(int page,Long id_from,Long id_to){

        setSize(6);
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Message>messagePage=repositoryMessage.findAllPageable(pageable,id_from,id_to);
        return messagePage.getContent().collect(Collectors.toSet());




//        List<Message> messageList = new ArrayList<>();
//        messages.forEach(messageList::add);
//        messageList.sort(Comparator.comparing(Message::getId));

    }

    public Long findTo(Long id) {
        return repositoryMessage.findTo(id);

    }
}
