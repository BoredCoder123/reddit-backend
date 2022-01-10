package com.example.redditbackend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BanRequest {
    private Integer userToBeBanned;
    private Integer communityFromWhichToBeBanned;
    private Integer personBanning;
    private String banReason;
}
