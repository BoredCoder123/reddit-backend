package com.example.redditbackend.controller;

import com.example.redditbackend.entity.UserTable;
import com.example.redditbackend.request.AddPostRequest;
import com.example.redditbackend.request.PostCommentRequest;
import com.example.redditbackend.service.PostsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
public class PostsController {

    @Autowired
    private PostsService postsService;

    @GetMapping("/test-posts")
    public ResponseEntity<UserTable> testPosts(){
        return new ResponseEntity<>(postsService.testPosts(), HttpStatus.OK);
    }

    @PostMapping("/meme")
    public ResponseEntity<Object> addPost(@RequestBody AddPostRequest addPostRequest){
        try{
            return new ResponseEntity<>(postsService.addPosts(addPostRequest), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>("Unable to add a post", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/like-post/{userId}/{postId}")
    public ResponseEntity<Object> likePost(@PathVariable Integer userId, @PathVariable Integer postId){
        try{
            return new ResponseEntity<>(postsService.likePost(userId, postId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>("Unable to like post", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("dislike-post/{userId}/{postId}")
    public ResponseEntity<Object> dislikePost(@PathVariable Integer userId, @PathVariable Integer postId){
        try{
            return new ResponseEntity<>(postsService.dislikePost(userId, postId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>("Unable to dislike post", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<Object> postComment(@RequestBody PostCommentRequest postCommentRequest){
        try{
            return new ResponseEntity<>(postsService.postComment(postCommentRequest), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>("Unable to post comment", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/like-comment/{userId}/{commentId}")
    public ResponseEntity<Object> likeComment(@PathVariable Integer userId, @PathVariable Integer commentId){
        try{
            return new ResponseEntity<>(postsService.likeComment(userId, commentId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>("Unable to like comment", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/dislike-comment/{userId}/{commentId}")
    public ResponseEntity<Object> dislikeComment(@PathVariable Integer userId, @PathVariable Integer commentId){
        try{
            return new ResponseEntity<>(postsService.dislikeComment(userId, commentId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>("Unable to dislike comment", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/single-post/{userId}/{postId}")
    public ResponseEntity<Object> viewSinglePost(@PathVariable Integer userId, @PathVariable Integer postId){
        try{
            return new ResponseEntity<>(postsService.viewSinglePost(userId, postId), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>("Unable to view post", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/dashboard/top-all-time/{userId}/{pageNumber}")
    public ResponseEntity<Object> viewDashBoardTopAllTime(@PathVariable Integer userId, @PathVariable Integer pageNumber){
        try{
            return new ResponseEntity<>(postsService.viewDashboard(userId, "top-all-time", pageNumber), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>("Unable to fetch top posts", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/dashboard/top-today/{userId}/{pageNumber}")
    public ResponseEntity<Object> viewDashBoardTopToday(@PathVariable Integer userId, @PathVariable Integer pageNumber){
        try{
            return new ResponseEntity<>(postsService.viewDashboard(userId, "top-today", pageNumber), HttpStatus.OK);
        }catch (Exception e){
            log.error(e);
            return new ResponseEntity<>("Unable to fetch top posts", HttpStatus.CONFLICT);
        }
    }
}
