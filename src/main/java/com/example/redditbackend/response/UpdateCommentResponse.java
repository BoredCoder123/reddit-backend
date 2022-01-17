package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentResponse {
    private Integer userId;
    private Integer commentId;
    private String username;
    private String newComment;
    private Integer postId;
}
