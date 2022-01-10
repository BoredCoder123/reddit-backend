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
public class DemoteFromCoOwnerResponse {
    private Integer coOwnerUserCommunityId;
    private Integer userId;
    private Integer communityId;
    private Date becomeCoOwnerDate;
    private Boolean currentlyActive;
    private Date statusChangeDate;
    private Boolean isCurrentlyMod;
}
