package com.example.laborator7.domain;

import java.time.LocalDateTime;

public class DisplayableRequest {
    private Long id;
    private Long userId1;
    private Long userId2;
    private LocalDateTime date;
    private String status;

    public DisplayableRequest(Long id, Long userId1, Long userId2, LocalDateTime date, String status) {
        this.id = id;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.date = date;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId1() {
        return userId1;
    }

    public Long getUserId2() {
        return userId2;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
