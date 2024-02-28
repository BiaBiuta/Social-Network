package com.example.laborator7.utils.events.friendship;

import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.utils.events.Event;


public class FriendTaskChangeEvent implements Event {
    private ChangeEventTypeF type;
    private Friendship task;
    private Friendship oldTask;
    public FriendTaskChangeEvent(ChangeEventTypeF type, Friendship task) {
        this.task=task;
        this.type=type;
    }

    public FriendTaskChangeEvent(ChangeEventTypeF type, Friendship task, Friendship oldTask) {
        this.type = type;
        this.task = task;
        this.oldTask = oldTask;
    }

    public Friendship getTask() {
        return task;
    }
}
