package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCommentResponse {
    private Integer userId;
    private String username;
    private Integer postId;
    private Integer commentIdDeleted;
    private List<Integer> likesDeleted;
    private List<Integer> dislikeDeleted;
}
