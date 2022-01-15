package com.example.redditbackend.service;

import com.example.redditbackend.entity.*;
import com.example.redditbackend.repository.*;
import com.example.redditbackend.request.AddPostRequest;
import com.example.redditbackend.request.PostCommentRequest;
import com.example.redditbackend.response.*;
import com.example.redditbackend.utility.CheckExistence;
import com.example.redditbackend.utility.PostType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Autowired
    private CommentsTableRepository commentRepo;

    public UserTable testPosts() {
        return checkExistence.postTest();
    }

    @Transactional
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

    @Transactional
    public LikeResponse likePost(Integer userId, Integer postId) throws Exception {
        try{
            LikeResponse response = new LikeResponse();
            UserTable user = checkExistence.checkUserExists(userId);
            PostTable post = checkExistence.checkPostsExists(postId);
            LikesTable existingLike = likesRepo.findByPostIdAndUserId(post, user);
            if(existingLike == null){
                DislikesTable checkDislike = dislikesRepo.findByPostIdAndUserId(post, user);
                if(checkDislike !=null){
                    dislikesRepo.delete(checkDislike);
                    post.setDislikes(post.getDislikes()-1);
                    response.setDislikeDeleted(checkDislike.getDislikesId());
                }
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

    @Transactional
    public DislikeResponse dislikePost(Integer userId, Integer postId) throws Exception{
        try{
            DislikeResponse response = new DislikeResponse();
            UserTable user = checkExistence.checkUserExists(userId);
            PostTable post = checkExistence.checkPostsExists(postId);
            DislikesTable dislike = dislikesRepo.findByPostIdAndUserId(post, user);
            LikesTable like = likesRepo.findByPostIdAndUserId(post, user);
            DislikesTable savedDislike;
            if(dislike == null){
                if(like!=null){
                    likesRepo.delete(like);
                    post.setLikes(post.getLikes()-1);
                    response.setLikeDeleted(like.getLikesId());
                }
                DislikesTable newDislike = new DislikesTable();
                newDislike.setPostId(post);
                newDislike.setDateAdded(new Date());
                newDislike.setUserId(user);
                savedDislike = dislikesRepo.save(newDislike);
                response.setDislikedAdded(savedDislike.getDislikesId());
                post.setDislikes(post.getDislikes()+1);
            }else{
                dislikesRepo.delete(dislike);
                post.setDislikes(post.getDislikes()-1);
                response.setDislikeRemoved(dislike.getDislikesId());
            }
            PostTable savedPost = postRepo.save(post);
            response.setPostId(savedPost.getPostId());
            response.setDislikeCount(savedPost.getDislikes());
            response.setUserId(user.getUserId());
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to dislike post because: "+e);
        }
    }

    @Transactional
    public PostCommentResponse postComment(PostCommentRequest postCommentRequest) throws Exception{
        try{
            UserTable user = checkExistence.checkUserExists(postCommentRequest.getUserId());
            PostTable post;
            CommentsTable comment;
            CommentsTable newComment = new CommentsTable();
            if(postCommentRequest.getPostId()!=null && postCommentRequest.getCommentId()!=null)
                throw new Exception("Post id and comment id both exists, unable to distinguish which to save");
            else if(postCommentRequest.getPostId()!=null) {
                post = checkExistence.checkPostsExists(postCommentRequest.getPostId());
                newComment.setPostId(post);

            }
            else if(postCommentRequest.getCommentId()!=null) {
                comment = checkExistence.checkCommentExists(postCommentRequest.getCommentId());
                newComment.setCommentLinkedTo(comment);
            }
            else
                throw new Exception("Both post and comment id not present");
            newComment.setDateAdded(new Date());
            newComment.setUserId(user);
            newComment.setLikes(1);
            newComment.setDislikes(0);
            newComment.setIsCommentBanned(false);
            newComment.setBanReason(null);
            newComment.setCommentText(postCommentRequest.getCommentData());
            CommentsTable savedComment = commentRepo.save(newComment);
            LikesTable newLike = new LikesTable();
            newLike.setCommentId(savedComment);
            newLike.setUserId(user);
            newLike.setDateAdded(new Date());
            LikesTable savedLike = likesRepo.save(newLike);
            PostCommentResponse response;
            if(postCommentRequest.getPostId()!=null) {
                response = new PostCommentResponse(savedComment.getUserId().getUserId(), savedComment.getPostId().getPostId(),
                        null, savedComment.getCommentsId(), savedComment.getCommentText(), savedLike.getLikesId());
            }else{
                response = new PostCommentResponse(savedComment.getUserId().getUserId(), null,
                        savedComment.getCommentLinkedTo().getCommentsId(), savedComment.getCommentsId(), savedComment.getCommentText(), savedLike.getLikesId());
            }
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to post comment because: "+e);
        }
    }

    @Transactional
    public LikeResponse likeComment(Integer userId, Integer commentId) throws Exception {
        try{
            LikeResponse response = new LikeResponse();
            UserTable user = checkExistence.checkUserExists(userId);
            CommentsTable comment = checkExistence.checkCommentExists(commentId);
            response.setUserId(user.getUserId());
            response.setCommentId(comment.getCommentsId());

            LikesTable like = likesRepo.findByCommentIdAndUserId(comment, user);
            if(like==null){
                LikesTable newLike = new LikesTable();
                newLike.setCommentId(comment);
                newLike.setDateAdded(new Date());
                newLike.setUserId(user);
                LikesTable savedLike = likesRepo.save(newLike);
                response.setLikedAdded(savedLike.getLikesId());
                DislikesTable dislike = dislikesRepo.findByCommentIdAndUserId(comment, user);
                if(dislike!=null){
                    dislikesRepo.delete(dislike);
                    response.setDislikeDeleted(dislike.getDislikesId());
                    comment.setDislikes(comment.getDislikes()-1);
                }
                comment.setLikes(comment.getLikes()+1);
            }else{
                likesRepo.delete(like);
                response.setLikeRemoved(like.getLikesId());
                comment.setLikes(comment.getLikes()-1);
            }
            CommentsTable savedComment = commentRepo.save(comment);
            response.setLikeCount(savedComment.getLikes());
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to like comment because: "+e);
        }
    }

    @Transactional
    public DislikeResponse dislikeComment(Integer userId, Integer commentId) throws Exception{
        try{
            DislikeResponse response = new DislikeResponse();
            UserTable user  = checkExistence.checkUserExists(userId);
            CommentsTable comment = checkExistence.checkCommentExists(commentId);
            response.setUserId(user.getUserId());
            response.setCommentId(comment.getCommentsId());

            DislikesTable dislike = dislikesRepo.findByCommentIdAndUserId(comment, user);
            if(dislike == null){
                DislikesTable newDislike = new DislikesTable();
                newDislike.setCommentId(comment);
                newDislike.setDateAdded(new Date());
                newDislike.setUserId(user);
                DislikesTable savedDislike = dislikesRepo.save(newDislike);
                response.setDislikedAdded(savedDislike.getDislikesId());
                LikesTable like = likesRepo.findByCommentIdAndUserId(comment, user);
                if(like != null){
                    likesRepo.delete(like);
                    response.setLikeDeleted(like.getLikesId());
                    comment.setLikes(comment.getLikes()+1);
                }
                comment.setDislikes(comment.getDislikes()+1);
            }else{
                dislikesRepo.delete(dislike);
                response.setDislikeRemoved(dislike.getDislikesId());
                comment.setDislikes(comment.getDislikes()-1);
            }
            CommentsTable savedComment = commentRepo.save(comment);
            response.setDislikeCount(savedComment.getDislikes());
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to dislike comment because: "+e);
        }
    }
}
