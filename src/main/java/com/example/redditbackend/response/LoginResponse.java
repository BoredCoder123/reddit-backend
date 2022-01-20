package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Integer userId;
    private String username;
    private String loginResponse;
    private String jwt;

    public LoginResponse(String loginResponse, Integer userId, String username) {
        this.loginResponse = loginResponse;
        this.username = username;
        this.userId = userId;
    }
}
