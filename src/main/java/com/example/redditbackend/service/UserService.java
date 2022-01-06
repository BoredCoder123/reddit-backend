package com.example.redditbackend.service;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.UserTable;
import com.example.redditbackend.repository.CommunityTableRepository;
import com.example.redditbackend.repository.UserTableRepository;
import com.example.redditbackend.request.CommunityRequest;
import com.example.redditbackend.request.LoginRequest;
import com.example.redditbackend.request.RegisterRequest;
import com.example.redditbackend.response.CommunityResponse;
import com.example.redditbackend.utility.SHA256;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.annotation.ExceptionProxy;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserService {
    @Autowired
    private UserTableRepository userTableRepository;

    @Autowired
    private CommunityTableRepository communityTableRepository;

    public String register(RegisterRequest registerRequest) throws Exception{
        try{
            UserTable checkExistingUser = userTableRepository.findByEmail(registerRequest.getEmail());
            if(checkExistingUser != null)
                throw new Exception("User with email "+registerRequest.getEmail()+" already exists");
            checkExistingUser = userTableRepository.findByUsername(registerRequest.getUsername());
            if(checkExistingUser != null)
                throw new Exception("User with username "+registerRequest.getUsername()+" already exists");
            UserTable newUser = new UserTable();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setHashedPassword(SHA256.toHexString(SHA256.getSHA(registerRequest.getPassword())));
            newUser.setJoinDate(new Date());
            newUser.setLastLoggedIn(new Date());
            userTableRepository.save(newUser);
            return "User registered successfully";
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception(e.toString());
        }
    }

    public String login(LoginRequest loginRequest) throws Exception{
        try{
            UserTable checkUser = userTableRepository.findByUsername(loginRequest.getUsername());
            if(checkUser == null)
                throw new Exception("Unable to find username");
            String hashedPassword = SHA256.toHexString(SHA256.getSHA(loginRequest.getPassword()));
            if(hashedPassword.equals(checkUser.getHashedPassword()))
                return "User logged in";
            throw new Exception("Password don't match");
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception(e.toString());
        }
    }

    public String createComponent(CommunityRequest communityRequest) throws Exception{
        try{
            Optional<UserTable> checkUser = userTableRepository.findById(communityRequest.getCreatorId());
            if(!checkUser.isPresent())
                throw new Exception("Unable to find creator id");
            CommunityTable checkExistingCommunity = communityTableRepository.findByCommunityName(communityRequest.getCommunityName());
            if(checkExistingCommunity != null)
                throw new Exception("Community name already exists");
            CommunityTable communityTable = new CommunityTable();
            communityTable.setCommunityName(communityRequest.getCommunityName());
            communityTable.setCurrentOwner(checkUser.get());
            communityTable.setCreationDate(new Date());
            communityTable.setRules(communityRequest.getRules());
            communityTable.setCreatorId(checkUser.get());
            log.error(checkUser.get().getUserId());
            communityTableRepository.save(communityTable);
            return "Community created";
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to create community due to: "+e.toString());
        }
    }

    public List<CommunityResponse> getAllCommunities() throws Exception{
        try{
            List<CommunityTable> communityTableList =  communityTableRepository.findAll();
            List<CommunityResponse> result = new ArrayList<>();
            for (CommunityTable c:communityTableList) {
                CommunityResponse cr = new CommunityResponse(c.getCommunityId(), c.getCommunityName(),
                        c.getCreationDate(), c.getRules(), c.getCreatorId().getUserId(), c.getCurrentOwner().getUserId());
                result.add(cr);
            }
            return result;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to fetch community due to: "+e.toString());
        }
    }

    public List<CommunityResponse> getAllCommunities(String username) throws Exception{
        try{
            UserTable userTable = userTableRepository.findByUsername(username);
            if(userTable == null)
                throw new Exception("Unable to find username");
            List<CommunityResponse> result = new ArrayList<>();
            List<CommunityTable> communityTableList = communityTableRepository.findByCurrentOwner(userTable);
            for (CommunityTable c:communityTableList) {
                CommunityResponse cr = new CommunityResponse(c.getCommunityId(), c.getCommunityName(),
                        c.getCreationDate(), c.getRules(), c.getCreatorId().getUserId(), c.getCurrentOwner().getUserId());
                result.add(cr);
            }
            return result;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to fetch community due to: "+e.toString());
        }
    }

    public List<CommunityResponse> getAllCommunitiesByCreatorId(String username) throws Exception{
        try{
            UserTable userTable = userTableRepository.findByUsername(username);
            if(userTable == null)
                throw new Exception("Unable to find username");
            List<CommunityResponse> result = new ArrayList<>();
            List<CommunityTable> communityTableList = communityTableRepository.findByCreatorId(userTable);
            for (CommunityTable c:communityTableList) {
                CommunityResponse cr = new CommunityResponse(c.getCommunityId(), c.getCommunityName(),
                        c.getCreationDate(), c.getRules(), c.getCreatorId().getUserId(), c.getCurrentOwner().getUserId());
                result.add(cr);
            }
            return result;
        }catch (Exception e){
            log.error(e.toString());
            throw new Exception("Unable to fetch community due to: "+e.toString());
        }
    }
}
