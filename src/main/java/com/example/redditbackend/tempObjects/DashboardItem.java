package com.example.redditbackend.tempObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardItem {
    private Integer postId;
    private String postTitle;
    private Integer communityId;
    private String communityName;
    private String userPosted;
    private Boolean isUserOp;
    private Integer likes;
    private Integer dislikes;
    private Integer comments;
    private Boolean hasUserLiked;
    private Boolean hasUserDisliked;
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
