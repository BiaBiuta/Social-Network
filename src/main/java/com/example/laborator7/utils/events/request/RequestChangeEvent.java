package com.example.laborator7.utils.events.request;

import com.example.laborator7.domain.RequestFriendship;
import com.example.laborator7.utils.events.Event;

public class RequestChangeEvent implements Event {
    private StatusChange type;
    private RequestFriendship task;
    private RequestFriendship oldTask;
    public RequestChangeEvent(StatusChange type, RequestFriendship task) {
        this.task=task;
        this.type=type;
    }

    public RequestChangeEvent(StatusChange type, RequestFriendship task, RequestFriendship oldTask) {
        this.type = type;
        this.task = task;
        this.oldTask = oldTask;
    }

    public RequestFriendship getTask() {
        return task;
    }
}
