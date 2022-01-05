package com.example.redditbackend.controller;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.request.CommunityRequest;
import com.example.redditbackend.request.LoginRequest;
import com.example.redditbackend.request.RegisterRequest;
import com.example.redditbackend.response.HeartbeatResponse;
import com.example.redditbackend.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Log4j2
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/heartbeat")
    public ResponseEntity<HeartbeatResponse> heartbeat(){
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse("Working", new Date());
        return new ResponseEntity<HeartbeatResponse>(heartbeatResponse, HttpStatus.OK);
    }

    @PostMapping("/u/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest){
        try{
            String response = userService.register(registerRequest);
            return new ResponseEntity<String>(response, HttpStatus.OK);
        }catch(Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to register", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/u/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        try{
            String response = userService.login(loginRequest);
            return new ResponseEntity<String>(response, HttpStatus.OK);
        }catch(Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to login", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/create-community")
    public ResponseEntity<String> createComponent(@RequestBody CommunityRequest communityRequest){
        try{
            String response = userService.createComponent(communityRequest);
            return new ResponseEntity<String>(response, HttpStatus.OK);
        }catch(Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to create community", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/community")
    public ResponseEntity getAllCommunities(){
        try{
            return new ResponseEntity<>(userService.getAllCommunities(), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to fetch all communities", HttpStatus.CONFLICT);
        }
    }
}
