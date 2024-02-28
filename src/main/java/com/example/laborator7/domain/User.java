package com.example.laborator7.domain;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class User extends Entity<Long>  {
    private String firstName;
    private String lastName;
    private String password;
    private List<User> friends;
    public User(String firstName,String lastName,String password){
        this.firstName=firstName;
        this.lastName=lastName;
        this.password=password;
        this.friends=new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        StringJoiner friendsJoiner = new StringJoiner(", ", "friends=[", "]");
        friends.forEach(friend -> friendsJoiner.add(friend.getLastName()));

        return new StringJoiner(", ", "User{", "}")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'").add("' id:"+getId()+"'")
                .add(friendsJoiner.toString())
                .toString();
    }
    @Override
    public boolean equals(Object obj){
        if(this==obj){
            return true;
        }
        if(!(obj instanceof User)){
            return false;
        }
        User objectTransformed = (User)obj;
        return this.equals(objectTransformed.getFriends()) &&
                this.equals(objectTransformed.getFirstName()) &&
                this.equals(objectTransformed.getLastName()) && this.equals(objectTransformed.getPassword());

    }
    @Override
    public int hashCode(){return Objects.hash(getFirstName(),getLastName(),getFriends());}
}
