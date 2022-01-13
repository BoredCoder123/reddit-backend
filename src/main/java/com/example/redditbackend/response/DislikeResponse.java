package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DislikeResponse {
    private Integer userId;
    private Integer postId;
    private Integer commentId;
    private Integer dislikedAdded;
    private Integer dislikeRemoved;
    private Integer dislikeCount;
    private Integer likeDeleted;
}
