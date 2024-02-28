package com.example.laborator7.utils.events;

import com.example.laborator7.domain.Message;
import com.example.laborator7.utils.events.user.ChangeEventType;

public class MessageTaskChangeEvent implements Event {
    private ChangeMessageType type;
    private Message task;
    private Message oldTask;

    public MessageTaskChangeEvent(ChangeMessageType type, Message task) {
        this.task = task;
        this.type = type;
    }

    public MessageTaskChangeEvent(ChangeMessageType type, Message task, Message oldTask) {
        this.type = type;
        this.task = task;
        this.oldTask = oldTask;
    }

    public ChangeMessageType getType() {
        return type;
    }

    public Message getOldTask() {
        return oldTask;
    }

    public Message getTask() {
        return task;
    }
}
