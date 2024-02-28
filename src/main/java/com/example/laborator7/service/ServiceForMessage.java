package com.example.laborator7.service;

import com.example.laborator7.domain.Message;
import com.example.laborator7.utils.events.MessageTaskChangeEvent;
import com.example.laborator7.utils.observer.Observable;

import java.util.Date;

public interface ServiceForMessage extends Observable<MessageTaskChangeEvent> {
    Message addMessage(Message m);
    Message deleteMessage(Long id);
    Message updateMessage(Message m);
    Iterable<Message> getAllMessages();
}
