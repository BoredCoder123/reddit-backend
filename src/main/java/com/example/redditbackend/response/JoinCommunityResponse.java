package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinCommunityResponse {

    private Integer normalUserCommunityId;
    private Integer userId;
    private Integer communityId;
    private Date joinDate;
    private Boolean isUserBanned;
}
