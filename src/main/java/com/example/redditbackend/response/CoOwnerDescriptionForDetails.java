package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoOwnerDescriptionForDetails {
    private Integer coOwnerId;
    private String coOwnerUsername;
    private Boolean isCurrentlyActive;
}
