package com.example.laborator7.utils.events.user;

import com.example.laborator7.domain.User;
import com.example.laborator7.utils.events.Event;
import com.example.laborator7.utils.events.user.ChangeEventType;

public class UserTaskChangeEvent implements Event {
    private ChangeEventType type;
    private User task;
    private User oldTask;
    public UserTaskChangeEvent(ChangeEventType type, User task) {
        this.task=task;
        this.type=type;
    }

    public UserTaskChangeEvent(ChangeEventType type, User task, User oldTask) {
        this.type = type;
        this.task = task;
        this.oldTask = oldTask;
    }

    public User getTask() {
        return task;
    }
}
