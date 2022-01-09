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
public class PromoteToModResponse {
    private Integer modUserId;
    private Integer userId;
    private Integer communityTable;
    private Date becomeModDate;
    private Integer promotedBy;
    private Boolean currentlyActiveMod;
    private Date statusChange;
}
