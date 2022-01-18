package com.example.redditbackend.tempObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsMade {
    private Integer postId;
    private String postTitle;
    private Integer commentId;
    private String commentString;
    private Date commentCreatedOn;
}
