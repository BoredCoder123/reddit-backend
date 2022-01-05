package com.example.redditbackend.service;

import com.example.redditbackend.entity.UserTable;
import com.example.redditbackend.repository.UserTableRepository;
import com.example.redditbackend.request.LoginRequest;
import com.example.redditbackend.request.RegisterRequest;
import com.example.redditbackend.utility.SHA256;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
public class UserService {
    @Autowired
    private UserTableRepository userTableRepository;

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
}
