package com.example.redditbackend.controller;

import com.example.redditbackend.service.PostsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class PostsController {

    @Autowired
    private PostsService postsService;

    @GetMapping("/test-posts")
    public String testPosts(){
        return postsService.testPosts();
    }
}
