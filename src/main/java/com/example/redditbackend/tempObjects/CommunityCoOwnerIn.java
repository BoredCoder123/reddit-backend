package com.example.redditbackend.tempObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityCoOwnerIn {
    private Integer communityId;
    private String communityName;
    private Date dateBecameCoOwner;
}
