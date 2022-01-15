package com.example.redditbackend.tempObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SinglePostComment {
    private String commentText;
    private Boolean isOp;
    private String commentPoster;
    private String whenPosted;
    private Integer likes;
    private Integer dislikes;
    //TODO linked comments
//    private List<SinglePostComment> commentLinked;
}
