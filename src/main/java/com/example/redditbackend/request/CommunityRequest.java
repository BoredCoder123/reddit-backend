package com.example.redditbackend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityRequest {
    private String communityName;
    private List<String> rules;
    private Integer creatorId;
    private String communityDescription;
}
