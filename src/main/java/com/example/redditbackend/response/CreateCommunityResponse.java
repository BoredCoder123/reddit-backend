package com.example.redditbackend.response;

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
public class CreateCommunityResponse {
    private Integer communityId;
    private String communityName;
    private Date creationDate;
    private Integer creatorId;
    private Integer currentOwnerId;
    private Integer normalUserId;
    private String communityDescription;
    private List<String> rules;
}
