package com.example.laborator7.domain;

import java.util.List;

public class Conversation {
    List<Message>messageList;
    public Conversation(List<Message>messageList){
        this.messageList=messageList;
    }
}
