package com.example.redditbackend.response;

import com.example.redditbackend.tempObjects.SinglePostComment;
import com.example.redditbackend.utility.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SinglePostResponse {
    private String userPosted;
    private String postTitle;
    private PostType postType;
    private Boolean isUserOp;
    private String whenPosted;
    private Integer likes;
    private Integer dislikes;
    private Boolean isLiked;
    private Boolean isDisliked;
    private List<SinglePostComment> comments;
    //Image
    private String url;
    private byte[] imageSource;
    //Link
    private String linkUrl;
    //Text
    private String textDescription;
    //Video
    private String videoUrl;
}
