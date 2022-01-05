package com.example.redditbackend.controller;

import com.example.redditbackend.request.RegisterRequest;
import com.example.redditbackend.response.HeartbeatResponse;
import com.example.redditbackend.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Log4j2
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/heartbeat")
    public ResponseEntity heartbeat(){
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse("Working", new Date());
        return new ResponseEntity(heartbeatResponse, HttpStatus.OK);
    }

    @PostMapping("/u/register")
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest){
        try{
            String response = userService.register(registerRequest);
            return new ResponseEntity(response, HttpStatus.OK);
        }catch(Exception e){
            log.error(e.toString());
            return new ResponseEntity("Unable to register", HttpStatus.CONFLICT);
        }
    }
}
