package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveCommunityResponse {
    private Integer userId;
    private Integer communityId;
    private Integer normalUserIdDeleted;
    private Integer modUserIdDeleted;
    private Integer coOwnerIdDeleted;
}
