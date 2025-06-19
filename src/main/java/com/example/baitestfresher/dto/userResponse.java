package com.example.baitestfresher.dto;

import com.example.baitestfresher.status.UserStatus;

import lombok.Data;

@Data
public class userResponse {
    private Long id;
    private String name;
    private String userName;
    private String email;
    private String phoneNumber;
    private String avatar;
    private UserStatus status;
}
