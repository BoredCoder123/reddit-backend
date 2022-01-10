package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModUserDescriptionForDetails {
    private Integer modId;
    private String modUsername;
    private Boolean isCurrentlyActive;
}
