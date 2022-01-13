package com.example.redditbackend.request;

import com.example.redditbackend.utility.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddPostRequest {
    private Integer userId;
    private Integer communityId;
    private PostType postType;
    private String postTitle;
    private String imageUrl;
    private byte[] imageSource;
    private String linkUrl;
    private String textDescription;
    private String videoUrl;
}
