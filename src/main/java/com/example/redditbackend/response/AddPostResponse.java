package com.example.redditbackend.response;

import com.example.redditbackend.utility.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddPostResponse {
    private Integer postId;
    private Integer likes;
    private Integer dislikes;
    private Date postDate;
    private PostType postType;
    private Integer communityId;
    private Integer userId;
    private Integer imageId;
    private Integer linkId;
    private Integer textId;
    private Integer videoId;
    private Integer likesId;
}
