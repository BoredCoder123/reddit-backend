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
public class TransferOwnershipResponse {
    private Integer newOwner;
    private Integer oldOwner;
    private Integer communityId;
    private String communityName;
    private Date transferDate;
}
