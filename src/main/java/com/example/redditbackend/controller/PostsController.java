package com.example.redditbackend.controller;

import com.example.redditbackend.entity.UserTable;
import com.example.redditbackend.request.AddPostRequest;
import com.example.redditbackend.service.PostsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class PostsController {

    @Autowired
    private PostsService postsService;

    @GetMapping("/test-posts")
    public ResponseEntity<UserTable> testPosts(){
        return new ResponseEntity<>(postsService.testPosts(), HttpStatus.OK);
    }

    @PostMapping("/content")
    public ResponseEntity addPost(@RequestBody AddPostRequest addPostRequest){
        try{
            return new ResponseEntity(postsService.addPosts(addPostRequest), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity("Unable to add a post", HttpStatus.CONFLICT);
        }
    }
}
