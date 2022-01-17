package com.example.redditbackend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BanPostRequest {
    private Integer bannerId;
    private Integer postId;
    private String banReason;
}
