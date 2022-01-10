package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NormalUserDescriptionForDetails {
    private Integer normalUserId;
    private String normalUsername;
    private Boolean isUserBanned;
    private String banReason;
}
