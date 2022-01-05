package com.example.redditbackend.controller;

import com.example.redditbackend.response.HeartbeatResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class UserController {
    @GetMapping("/heartbeat")
    public ResponseEntity heartbeat(){
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse("Working", new Date());
        return new ResponseEntity(heartbeatResponse, HttpStatus.OK);
    }
}
