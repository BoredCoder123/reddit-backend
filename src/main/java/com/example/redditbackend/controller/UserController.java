package com.example.redditbackend.controller;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.NormalUserCommunityTable;
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
    public ResponseEntity createComponent(@RequestBody CommunityRequest communityRequest){
        try{
            return new ResponseEntity(userService.createComponent(communityRequest), HttpStatus.OK);
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

    @GetMapping("/community/{username}")
    public ResponseEntity getCommunitiesByUsername(@PathVariable String username){
        try{
            return new ResponseEntity<>(userService.getAllCommunities(username), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to fetch all communities", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/community/creator/{username}")
    public ResponseEntity getCommunitiesByCreatorId(@PathVariable String username){
        try{
            return new ResponseEntity<>(userService.getAllCommunitiesByCreatorId(username), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to fetch all communities", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/join-community/{userId}/{communityId}")
    public ResponseEntity joinCommunity(@PathVariable Integer userId, @PathVariable Integer communityId){
        try{
            return new ResponseEntity<NormalUserCommunityTable>(userService.joinCommunity(userId, communityId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to join community", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/promote-to-mod/{userId}/{communityId}/{promoterId}")
    public ResponseEntity promoteToMod(@PathVariable Integer userId, @PathVariable Integer communityId, @PathVariable Integer promoterId){
        try{
            return new ResponseEntity(userService.promoteToMod(userId, communityId, promoterId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to promote to mod", HttpStatus.CONFLICT);
        }
    }
}
