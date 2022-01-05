package com.example.redditbackend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityRequest {
    private String communityName;
    private String[] rules;
    private Integer creatorId;
}
