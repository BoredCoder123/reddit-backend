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
public class DeletePostResponse {
    private Integer postDeleted;
    private Integer userWhichDeleted;
    private List<Integer> commentsDeleted;
    private List<Integer> likesDeleted;
    private List<Integer> dislikesDeleted;
    private Integer linkDeleted;
    private Integer videoDeleted;
    private Integer textDeleted;
    private Integer imageDeleted;
}
