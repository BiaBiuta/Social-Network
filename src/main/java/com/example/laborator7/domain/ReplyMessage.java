package com.example.laborator7.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReplyMessage extends Message{
    Message id_message;
    public ReplyMessage(User from, List<User> all, LocalDateTime date, String message,Message id_message) {
        super(from, all, date, message);
        this.id_message=id_message;
    }

    public Message getId_message() {
        return id_message;
    }

    public void setId_message(Message id_message) {
        this.id_message = id_message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplyMessage that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id_message, that.id_message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id_message);
    }
}
