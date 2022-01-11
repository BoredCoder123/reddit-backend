package com.example.redditbackend.service;

import com.example.redditbackend.entity.UserTable;
import com.example.redditbackend.repository.UserTableRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PostsService {

    @Autowired
    private UserTableRepository userRepo;

    public String testPosts() {
        UserTable user = userRepo.findByUsername("test1");
        return "test";
    }
}
