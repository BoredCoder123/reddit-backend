package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikePostResponse {
    private Integer userId;
    private Integer postId;
    private Integer commentId;
    private Integer likedAdded;
    private Integer likeRemoved;
    private Integer likeCount;
}
