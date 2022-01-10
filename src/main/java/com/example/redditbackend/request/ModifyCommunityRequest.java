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
public class ModifyCommunityRequest {
    private Integer communityId;
    private String name;
    private List<String> rules;
    private String communityDescription;
}
