package com.example.redditbackend.response;

import com.example.redditbackend.entity.UserTable;
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
public class ModifyCommunityResponse {
    private Integer communityId;
    private String communityName;
    private String communityDescription;
    private Date creationDate;
    private List<String> rules;
    private UserTable creatorId;
    private UserTable currentOwner;
}
