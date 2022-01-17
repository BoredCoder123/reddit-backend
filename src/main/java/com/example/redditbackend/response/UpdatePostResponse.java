package com.example.redditbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostResponse {
    private Integer userId;
    private Integer postId;
    private String postTitle;
    //Image
    private Integer imageId;
    private String imageUrl;
    private byte[] imageSource;
    //Link
    private Integer linkId;
    private String linkUrl;
    //Text
    private Integer textId;
    private String textDescription;
    //Video
    private Integer videoId;
    private String videoUrl;
}
