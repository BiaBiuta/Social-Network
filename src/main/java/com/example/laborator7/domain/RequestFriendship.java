package com.example.laborator7.domain;

import com.example.laborator7.utils.events.request.StatusChange;

import java.time.LocalDateTime;

import static com.example.laborator7.utils.events.request.StatusChange.PENDING;

public class RequestFriendship extends Entity<Long>{
    private Long id;
   private Long id1;
   private Long id2;
   private LocalDateTime date;
   private String status;

    public RequestFriendship(Long id1, Long id2, LocalDateTime date) {
        this.id1 = id1;
        this.id2 = id2;
        this.date = date;
        this.status = "PENDING";
    }

    public Long getId1() {
        return id1;
    }

    public void setId1(Long id1) {
        this.id1 = id1;
    }

    public Long getId2() {
        return id2;
    }

    public void setId2(Long id2) {
        this.id2 = id2;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

   // @Override
//    public String toString() {
//        return  id +
//                " " + friend.getUserId1() +" " + friend.getUserId2()+ " " + friend.getDate() +" "+ status ;
//    }
}
