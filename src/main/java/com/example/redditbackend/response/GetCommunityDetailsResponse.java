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
public class GetCommunityDetailsResponse {
    private Integer communityId;
    private String communityName;
    private String communityDescription;
    private Date creationDate;
    private List<String> rules;
    private Integer creatorId;
    private String creatorName;
    private Integer currentOwnerId;
    private String currentOwnerName;
    private List<NormalUserDescriptionForDetails> listOfUsers;
    private List<ModUserDescriptionForDetails> listOfMods;
    private List<CoOwnerDescriptionForDetails> listOfCoOwners;
}

