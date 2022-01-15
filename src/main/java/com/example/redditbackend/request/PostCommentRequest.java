package com.example.redditbackend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentRequest {
    private Integer userId;
    private Integer postId;
    private Integer commentId;
    private String commentData;
}
