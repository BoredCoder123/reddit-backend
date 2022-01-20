package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String registerResponse;
    private String username;
    private Integer userId;
    private String jwt;

    public RegisterResponse(String registerResponse, String username, Integer userId){
        this.registerResponse = registerResponse;
        this.username = username;
        this.userId = userId;
    }
}
