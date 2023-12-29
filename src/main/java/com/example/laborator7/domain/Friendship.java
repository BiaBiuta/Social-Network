package org.example.domain;

import java.time.LocalDateTime;

public class Friendship extends Entity<CreatedTuple<Long,Long>> {
    CreatedTuple<Long,Long> friends;
    Long userId1;
    Long userId2;
    LocalDateTime date;
    public Friendship(Long userId1,Long userId2){
        this.friends=new CreatedTuple<Long,Long>(userId1,userId2);
        this.date=LocalDateTime.now();
        this.userId1=userId1;
        this.userId2=userId2;

    }

    public void setFriends(CreatedTuple<Long, Long> friends) {
        this.friends = friends;
    }

    public void setUserId1(Long userId1) {
        this.userId1 = userId1;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "userId1=" + userId1 +
                ", userId2=" + userId2 +
                ", date=" + date +
                '}';
    }
}
