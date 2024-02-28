package com.example.laborator7.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{
    private Long id;
    private User from;
    //private User to;
    private List<User> all;

    private LocalDateTime date;
    private String message;
    //private String toString;

    public Message(User from, List<User> all, LocalDateTime date, String message) {
        this.from = from;
        this.all=all;
        this.date =LocalDateTime.now();
        this.message = message;
    }


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getAll() {
        return all;
    }

    public void setAll(List<User> to) {
        this.all = to;
    }



    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message1)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getId(), message1.getId()) && Objects.equals(getFrom(), message1.getFrom()) && Objects.equals(getAll(), message1.getAll()) && Objects.equals(getDate(), message1.getDate()) && Objects.equals(getMessage(), message1.getMessage());
    }

//    public String getToString() {
//        return toString;
//    }
//
//    public void setToString(String toString) {
//        this.toString = toString;
//    }

//    public User getTo() {
//        return to;
//    }
//
//    public void setTo(User to) {
//        this.to = to;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getFrom(), getAll(), getDate(), getMessage());
    }

}
