package com.example.redditbackend.response;

import com.example.redditbackend.utility.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BanPostResponse {
    private Integer bannedBy;
    private String banningUsername;
    private Integer postId;
    private PostType postType;
    private Integer communityPostedIn;
    private String communityName;
    private String banReason;
    private Integer postPostedBy;
    private String postedUsername;
}
