package com.example.redditbackend.controller;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.NormalUserCommunityTable;
import com.example.redditbackend.request.*;
import com.example.redditbackend.response.HeartbeatResponse;
import com.example.redditbackend.response.LoginResponse;
import com.example.redditbackend.response.RegisterResponse;
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
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest){
        try{
            RegisterResponse response = userService.register(registerRequest);
            return new ResponseEntity<RegisterResponse>(response, HttpStatus.OK);
        }catch(Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to register", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/u/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest){
        try{
            LoginResponse response = userService.login(loginRequest);
            return new ResponseEntity(response, HttpStatus.OK);
        }catch(Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to login", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/create-community")
    public ResponseEntity createCommunity(@RequestBody CommunityRequest communityRequest){
        try{
            return new ResponseEntity(userService.createCommunity(communityRequest), HttpStatus.OK);
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
            return new ResponseEntity<>(userService.joinCommunity(userId, communityId), HttpStatus.OK);
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

    @PostMapping("/demote-from-mod/{userId}/{communityId}/{demoterId}")
    public ResponseEntity demoteFromMod(@PathVariable Integer userId, @PathVariable Integer communityId, @PathVariable Integer demoterId){
        try{
            return new ResponseEntity(userService.demoteFromMod(userId, communityId, demoterId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.toString());
            return new ResponseEntity<String>("Unable to demote from mod", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/promote-to-coowner/{userId}/{communityId}/{promoterId}")
    public ResponseEntity promoteToCoOwner(@PathVariable Integer userId, @PathVariable Integer communityId, @PathVariable Integer promoterId) {
        try{
            return new ResponseEntity(userService.promoteToCoOwner(userId, communityId, promoterId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e.toString());
            return new ResponseEntity("Unable to promote to co owner", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/demote-from-coowner/{userId}/{communityId}/{promoterId}/{toMod}")
    public ResponseEntity demoteFromCoOwner(@PathVariable Integer userId, @PathVariable Integer communityId, @PathVariable Integer promoterId, @PathVariable String toMod) {
        try{
            return new ResponseEntity(userService.demoteFromCoOwner(userId, communityId, promoterId, toMod), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity("Unable to demote from co owner", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/ban")
    public ResponseEntity banUser(@RequestBody BanRequest banRequest){
        try{
            return new ResponseEntity(userService.banUser(banRequest), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity("Unable to ban person", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/unban/{userId}/{communityId}/{unBanningId}")
    public ResponseEntity unBanUser(@PathVariable Integer userId, @PathVariable Integer communityId, @PathVariable Integer unBanningId){
        try{
            return new ResponseEntity(userService.unBanUser(userId, communityId, unBanningId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity("Unable to unban person", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/leave-community/{userId}/{communityId}")
    public ResponseEntity leaveCommunity(@PathVariable Integer userId, @PathVariable Integer communityId){
        try{
            return new ResponseEntity(userService.leaveCommunity(userId, communityId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity("Unable to leave community", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/community/{userId}/{communityId}")
    public ResponseEntity getCommunityDetails(@PathVariable Integer userId, @PathVariable Integer communityId){
        try{
            return new ResponseEntity(userService.getCommunityDetails(userId, communityId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity("Unable to fetch details", HttpStatus.OK);
        }
    }

    @PostMapping("/modify-community")
    public ResponseEntity modifyCommunity(@RequestBody ModifyCommunityRequest modifyCommunityRequest){
        try{
            return new ResponseEntity(userService.modifyCommunityRequest(modifyCommunityRequest), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity("Unable to modify details", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/community-for-user/{userId}")
    public ResponseEntity fetchCommunities(@PathVariable Integer userId){
        try{
            return new ResponseEntity(userService.fetchCommunities(userId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity("Unable to fetch communities", HttpStatus.CONFLICT);
        }
    }
    //Write controller for fetch communities by normal user, transfer ownership of community
}
