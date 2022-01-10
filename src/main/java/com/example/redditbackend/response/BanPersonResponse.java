package com.example.redditbackend.response;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.UserTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BanPersonResponse {
    private Integer normalUserCommunityId;
    private Integer userId;
    private Integer communityId;
    private Date joinDate;
    private Boolean isUserBanned;
    private String banReason;
    private Date dateBanned;
}
