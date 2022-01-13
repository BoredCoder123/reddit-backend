package com.example.redditbackend.service;

import com.example.redditbackend.entity.UserTable;
import com.example.redditbackend.repository.UserTableRepository;
import com.example.redditbackend.utility.CheckExistence;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PostsService {

    @Autowired
    private UserTableRepository userRepo;

    @Autowired
    private CheckExistence checkExistence;

    public UserTable testPosts() {
        return checkExistence.postTest();
    }
}
