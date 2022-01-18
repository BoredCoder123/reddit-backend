package com.example.redditbackend.response;

import com.example.redditbackend.tempObjects.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewFullHistoryResponse {
    private Integer userId;
    private String userName;
    private Date userCreatedOn;
    private List<CommunityCreated> communitiesCreated;
    private List<CommunityJoined> communityJoined;
    private List<CommunityModIn> communityModIn;
    private List<CommunityCoOwnerIn> communityCoOwnerIn;
    private List<PostsMade> postsMade;
    private List<PostsLiked> postsLiked;
    private List<PostsDisliked> postsDisliked;
    private List<CommentsMade> commentsMade;
    private List<CommentsLiked> commentsLiked;
    private List<CommentsDisliked> commentsDisliked;
}
