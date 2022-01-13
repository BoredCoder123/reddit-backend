package com.example.redditbackend.utility;

import com.example.redditbackend.entity.UserTable;
import com.example.redditbackend.repository.UserTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckExistence {
    @Autowired
    private UserTableRepository userRepo;

    public UserTable postTest(){
        return userRepo.findByUsername("test1");
    }
}
