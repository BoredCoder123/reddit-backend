package com.example.redditbackend.service;

import com.example.redditbackend.entity.*;
import com.example.redditbackend.repository.*;
import com.example.redditbackend.request.AddPostRequest;
import com.example.redditbackend.response.AddPostResponse;
import com.example.redditbackend.response.LikePostResponse;
import com.example.redditbackend.utility.CheckExistence;
import com.example.redditbackend.utility.PostType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
public class PostsService {

    @Autowired
    private PostTableRepository postRepo;

    @Autowired
    private CheckExistence checkExistence;

    @Autowired
    private LikesTableRepository likesRepo;

    @Autowired
    private DislikesTableRepository dislikesRepo;

    @Autowired
    private PostTypeImageRepository imageRepo;

    @Autowired
    private PostTypeLinkRepository linkRepo;

    @Autowired
    private PostTypeTextRepository textRepo;

    @Autowired
    private PostTypeVideoRepository videoRepo;

    public UserTable testPosts() {
        return checkExistence.postTest();
    }

    public AddPostResponse addPosts(AddPostRequest addPostRequest) throws Exception{
        try{
            if(addPostRequest.getPostType()!=PostType.IMAGE && addPostRequest.getPostType()!=PostType.LINK && addPostRequest.getPostType()!=PostType.TEXT &&
                    addPostRequest.getPostType()!=PostType.VIDEO)
                throw new Exception("Invalid post type");
            AddPostResponse response = new AddPostResponse();
            UserTable user = checkExistence.checkUserExists(addPostRequest.getUserId());
            CommunityTable community = checkExistence.checkCommExists(addPostRequest.getCommunityId());
            NormalUserCommunityTable userInComm = checkExistence.checkUserInComm(user, community);

            PostTable postTable = new PostTable(user, community, new Date(), addPostRequest.getPostType(), 1, 0, false);
            PostTable savedPost = postRepo.save(postTable);
            response.setPostId(savedPost.getPostId());
            response.setLikes(savedPost.getLikes());
            response.setPostDate(savedPost.getPostDate());
            response.setPostType(savedPost.getPostType());
            response.setCommunityId(savedPost.getCommunityId().getCommunityId());
            response.setUserId(savedPost.getUserPosted().getUserId());

            LikesTable likes = new LikesTable();
            likes.setUserId(user);
            likes.setPostId(savedPost);
            likes.setDateAdded(new Date());
            LikesTable savedLikes = likesRepo.save(likes);
            response.setLikesId(savedLikes.getLikesId());

            if(addPostRequest.getPostType() == PostType.IMAGE){
                PostTypeImage image = new PostTypeImage();
                image.setImageSource(addPostRequest.getImageSource());
                image.setPostId(savedPost);
                image.setUrl(addPostRequest.getImageUrl());
                image.setImageTitle(addPostRequest.getPostTitle());
                PostTypeImage savedImage = imageRepo.save(image);
                response.setImageId(savedImage.getImageId());
            }else if(addPostRequest.getPostType() == PostType.LINK){
                PostTypeLink link = new PostTypeLink();
                link.setPostId(savedPost);
                link.setLinkTitle(addPostRequest.getPostTitle());
                link.setLinkUrl(addPostRequest.getLinkUrl());
                PostTypeLink savedLink = linkRepo.save(link);
                response.setLinkId(savedLink.getLinkId());
            }else if(addPostRequest.getPostType() == PostType.TEXT){
                PostTypeText text = new PostTypeText();
                text.setPostId(savedPost);
                text.setTextDescription(addPostRequest.getTextDescription());
                text.setTextTitle(addPostRequest.getPostTitle());
                PostTypeText savedText = textRepo.save(text);
                response.setTextId(savedText.getTextId());
            }else if(addPostRequest.getPostType() == PostType.VIDEO){
                PostTypeVideo video = new PostTypeVideo();
                video.setPostId(savedPost);
                video.setVideoUrl(addPostRequest.getVideoUrl());
                video.setVideoTitle(addPostRequest.getPostTitle());
                PostTypeVideo savedVideo = videoRepo.save(video);
                response.setVideoId(savedVideo.getVideoId());
            }

            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to add posts because: "+e);
        }
    }

    public LikePostResponse likePost(Integer userId, Integer postId) throws Exception {
        try{
            LikePostResponse response = new LikePostResponse();
            UserTable user = checkExistence.checkUserExists(userId);
            PostTable post = checkExistence.checkPostsExists(postId);
            LikesTable existingLike = likesRepo.findByPostIdAndUserId(post, user);
            if(existingLike == null){
                LikesTable newLike = new LikesTable();
                newLike.setDateAdded(new Date());
                newLike.setPostId(post);
                newLike.setUserId(user);
                LikesTable savedLike = likesRepo.save(newLike);
                post.setLikes(post.getLikes()+1);
                PostTable savedPost = postRepo.save(post);
                response.setLikedAdded(savedLike.getLikesId());
                response.setLikeCount(savedPost.getLikes());
                response.setPostId(savedLike.getPostId().getPostId());
            }else{
                likesRepo.delete(existingLike);
                if(post.getLikes()>1)
                    post.setLikes(post.getLikes()-1);
                else
                    post.setLikes(0);
                PostTable savedPost = postRepo.save(post);
                response.setLikeRemoved(existingLike.getLikesId());
                response.setPostId(savedPost.getPostId());
            }
            response.setUserId(user.getUserId());
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to like post because: "+e);
        }
    }
}
