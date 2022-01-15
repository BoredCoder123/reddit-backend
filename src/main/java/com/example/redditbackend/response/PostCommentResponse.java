package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentResponse {
    private Integer userId;
    private Integer postCommentedOn;
    private Integer commentCommentedOn;
    private Integer commentId;
    private String commentData;
    private Integer likeId;
}
