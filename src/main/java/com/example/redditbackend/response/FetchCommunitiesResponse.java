package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FetchCommunitiesResponse {
    private Integer communityId;
    private String communityName;
    private List<String> rules;
    private String communityDescription;
}
