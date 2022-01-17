package com.example.redditbackend.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest {
    private Integer userId;
    private Integer postId;
    private String postTitle;
    //Image
    private String imageUrl;
    private byte[] imageSource;
    //Link
    private String linkUrl;
    //Text
    private String textDescription;
    //Video
    private String videoUrl;
}
