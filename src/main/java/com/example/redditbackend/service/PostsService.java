package com.example.redditbackend.service;

import com.example.redditbackend.entity.*;
import com.example.redditbackend.repository.*;
import com.example.redditbackend.request.AddPostRequest;
import com.example.redditbackend.request.BanPostRequest;
import com.example.redditbackend.request.PostCommentRequest;
import com.example.redditbackend.request.UpdateCommentRequest;
import com.example.redditbackend.response.*;
import com.example.redditbackend.tempObjects.DashboardItem;
import com.example.redditbackend.tempObjects.SinglePostComment;
import com.example.redditbackend.utility.CheckExistence;
import com.example.redditbackend.utility.Constants;
import com.example.redditbackend.utility.PostType;
import com.example.redditbackend.utility.Subtract2Times;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

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
                    comment.setLikes(comment.getLikes()-1);
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

    @Transactional
    public SinglePostResponse viewSinglePost(Integer userId, Integer postId) throws Exception {
        try{
            UserTable user = checkExistence.checkUserExists(userId);
            PostTable post = checkExistence.checkPostsExists(postId);
            SinglePostResponse response = new SinglePostResponse();
            response.setUserPosted(post.getUserPosted().getUsername());
            response.setPostType(post.getPostType());
            if(post.getPostType()==PostType.IMAGE){
                PostTypeImage image = imageRepo.findByPostId(post);
                response.setPostTitle(image.getImageTitle());
                response.setUrl(image.getUrl());
                response.setImageSource(image.getImageSource());
            }else if(post.getPostType()==PostType.LINK){
                PostTypeLink link = linkRepo.findByPostId(post);
                response.setPostTitle(link.getLinkTitle());
                response.setLinkUrl(link.getLinkUrl());
            }else if(post.getPostType()==PostType.TEXT){
                PostTypeText text = textRepo.findByPostId(post);
                response.setPostTitle(text.getTextTitle());
                response.setTextDescription(text.getTextDescription());
            }else if(post.getPostType()==PostType.VIDEO){
                PostTypeVideo video = videoRepo.findByPostId(post);
                response.setPostTitle(video.getVideoTitle());
                response.setVideoUrl(video.getVideoUrl());
            }else{
                throw new Exception("Invalid post type");
            }
            response.setIsUserOp(user.getUserId().equals(post.getUserPosted().getUserId()));
            response.setWhenPosted(Subtract2Times.subtract2Times(post.getPostDate()));
            response.setLikes(post.getLikes());
            response.setDislikes(post.getDislikes());
            LikesTable like = likesRepo.findByPostIdAndUserId(post, user);
            response.setIsLiked(like != null);
            DislikesTable dislike = dislikesRepo.findByPostIdAndUserId(post, user);
            response.setIsDisliked(dislike!=null);
            List<CommentsTable> firstLayerComments = commentRepo.findByPostId(post);
            if(firstLayerComments.size()==0)
                response.setComments(null);
            else {
                List<SinglePostComment> comments = new ArrayList<>();
                for (CommentsTable c : firstLayerComments) {
                    SinglePostComment newComment = new SinglePostComment(c.getCommentText(), c.getUserId().getUserId().equals(user.getUserId()),
                            c.getUserId().getUsername(), Subtract2Times.subtract2Times(c.getDateAdded()), c.getLikes(), c.getDislikes());
                    comments.add(newComment);
                }
                response.setComments(comments);
            }
            //Todo linked comments
//            List<SinglePostComment> listOfComments = new ArrayList<>();
//            HashSet<CommentsTable> firstLayerComment = new HashSet<>();
//            Stack<CommentsTable> commentStack = new Stack<>();
//            if(firstLayerComments.size()==0)
//                response.setComments(null);
//            else{
//                for(CommentsTable c: firstLayerComments){
//                    commentStack.push(c);
//                    firstLayerComment.add(c);
//                }
//                while(!commentStack.isEmpty()){
//                    CommentsTable poppedComment = commentStack.pop();
//                    SinglePostComment temp = new SinglePostComment(poppedComment.getCommentText(), poppedComment.getUserId().getUserId().equals(user.getUserId()),
//                            poppedComment.getUserId().getUsername(), Subtract2Times.subtract2Times(poppedComment.getDateAdded()),
//                            poppedComment.getLikes(), poppedComment.getDislikes(), null);
//                }
//                response.setComments(listOfComments);
//            }
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to show single post due to: "+e);
        }
    }

    @Transactional
    public DashboardResponse viewDashboard(Integer userId, String view, Integer pageNumber) throws Exception{
        try{
            DashboardResponse response = new DashboardResponse();
            UserTable user = checkExistence.checkUserExists(userId);
            response.setUserId(user.getUserId());
            response.setUsername(user.getUsername());
            int start = (pageNumber-1) * Constants.postsPerPage, end = pageNumber * Constants.postsPerPage;
            List<PostTable> dataFromDb;
            switch (view) {
                case "top-all-time":
                    dataFromDb = postRepo.findTopViewOfAllTime(start, end);
                    break;
                case "top-today":
                    dataFromDb = postRepo.findTopToday(start, end);
                    break;
                case "new":
                    dataFromDb = postRepo.findNew(start, end);
                    break;
                default:
                    throw new Exception("Invalid fetch type");
            }
            List<DashboardItem> posts = new ArrayList<>();
            if(dataFromDb.size()==0)
                return response;
            for(PostTable p : dataFromDb){
                DashboardItem item = new DashboardItem();
                item.setPostId(p.getPostId());
                item.setUserPosted(p.getUserPosted().getUsername());
                item.setIsUserOp(p.getUserPosted().getUserId().equals(userId));
                item.setLikes(p.getLikes());
                item.setDislikes(p.getDislikes());
                item.setCommunityId(p.getCommunityId().getCommunityId());
                item.setCommunityName(p.getCommunityId().getCommunityName());
                List<Object> getCounts = commentRepo.findCountOfComments(p.getPostId());
                item.setComments(Integer.parseInt(getCounts.get(0).toString()));
                LikesTable isLiked = likesRepo.findByPostIdAndUserId(p, user);
                if(isLiked !=null){
                    item.setHasUserLiked(true);
                }else{
                    item.setHasUserLiked(false);
                    DislikesTable isDisliked = dislikesRepo.findByPostIdAndUserId(p, user);
                    item.setHasUserDisliked(isDisliked != null);
                }
                if(p.getPostType()==PostType.TEXT){
                    PostTypeText text = textRepo.findByPostId(p);
                    item.setPostTitle(text.getTextTitle());
                    item.setTextDescription(text.getTextDescription());
                }else if(p.getPostType()==PostType.IMAGE){
                    PostTypeImage image = imageRepo.findByPostId(p);
                    item.setPostTitle(image.getImageTitle());
                    item.setUrl(image.getUrl());
                    item.setImageSource(image.getImageSource());
                }else if(p.getPostType()==PostType.LINK){
                    PostTypeLink link = linkRepo.findByPostId(p);
                    item.setPostTitle(link.getLinkTitle());
                    item.setLinkUrl(link.getLinkUrl());
                }else if(p.getPostType()==PostType.VIDEO){
                    PostTypeVideo video = videoRepo.findByPostId(p);
                    item.setPostTitle(video.getVideoTitle());
                    item.setVideoUrl(video.getVideoUrl());
                }
                posts.add(item);
            }
            response.setPosts(posts);
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to view dashboard because: "+e);
        }
    }

    @Transactional
    public DeletePostResponse deletePost(Integer userId, Integer postId) throws Exception {
        try{
            DeletePostResponse response = new DeletePostResponse();
            UserTable user = checkExistence.checkUserExists(userId);
            PostTable post = checkExistence.checkPostsExists(postId);
            if(!post.getUserPosted().getUserId().equals(user.getUserId()))
                throw new Exception("User is not the owner");
            response.setPostDeleted(post.getPostId());
            response.setUserWhichDeleted(user.getUserId());
            switch (post.getPostType()){
                case LINK:
                    PostTypeLink link = linkRepo.findByPostId(post);
                    linkRepo.delete(link);
                    response.setLinkDeleted(link.getLinkId());
                    break;
                case TEXT:
                    PostTypeText text = textRepo.findByPostId(post);
                    textRepo.delete(text);
                    response.setTextDeleted(text.getTextId());
                    break;
                case IMAGE:
                    PostTypeImage image = imageRepo.findByPostId(post);
                    imageRepo.delete(image);
                    response.setImageDeleted(image.getImageId());
                    break;
                case VIDEO:
                    PostTypeVideo video = videoRepo.findByPostId(post);
                    videoRepo.delete(video);
                    response.setVideoDeleted(video.getVideoId());
                    break;
                default:
                    throw new Exception("Invalid post type");
            }
            List<Integer> commentsList = new ArrayList<>();
            List<Integer> likesList = new ArrayList<>();
            List<Integer> dislikesList = new ArrayList<>();
            List<CommentsTable> commentsToBeDeleted = commentRepo.findByPostId(post);
            for(CommentsTable c : commentsToBeDeleted){
                commentRepo.delete(c);
                commentsList.add(c.getCommentsId());
            }
            List<LikesTable> likesToBeDeleted = likesRepo.findByPostId(post);
            for(LikesTable l : likesToBeDeleted){
                likesList.add(l.getLikesId());
                likesRepo.delete(l);
            }
            List<DislikesTable> dislikesToBeDeleted = dislikesRepo.findByPostId(post);
            for(DislikesTable d : dislikesToBeDeleted){
                dislikesRepo.delete(d);
                dislikesList.add(d.getDislikesId());
            }
            response.setCommentsDeleted(commentsList);
            response.setLikesDeleted(likesList);
            response.setDislikesDeleted(dislikesList);
            postRepo.delete(post);
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to delete post because: "+e);
        }
    }

    public BanPostResponse banPost(BanPostRequest banPostRequest) throws Exception{
        try{
            BanPostResponse response = new BanPostResponse();
            UserTable user = checkExistence.checkUserExists(banPostRequest.getBannerId());
            response.setBannedBy(user.getUserId());
            response.setBanningUsername(user.getUsername());
            PostTable post = checkExistence.checkPostsExists(banPostRequest.getPostId());
            if(post.getIsPostBanned())
                throw new Exception("Post is already banned");
            if(!checkExistence.checkIfUserIsModCoOrOwner(user, post.getCommunityId()))
                throw new Exception("Not enough permission to ban the post");
            post.setIsPostBanned(true);
            post.setBanDate(new Date());
            post.setBannedBy(user);
            post.setBanReason(banPostRequest.getBanReason());
            PostTable savedPost = postRepo.save(post);
            response.setPostId(savedPost.getPostId());
            response.setPostType(savedPost.getPostType());
            response.setCommunityPostedIn(savedPost.getCommunityId().getCommunityId());
            response.setCommunityName(savedPost.getCommunityId().getCommunityName());
            response.setBanReason(banPostRequest.getBanReason());
            response.setPostPostedBy(savedPost.getUserPosted().getUserId());
            response.setPostedUsername(savedPost.getUserPosted().getUsername());
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to ban post because: "+e);
        }
    }

    public DeleteCommentResponse deleteComment(Integer userId, Integer commentId) throws Exception {
        try{
            UserTable user = checkExistence.checkUserExists(userId);
            CommentsTable comment = checkExistence.checkCommentExists(commentId);
            if(!comment.getUserId().getUserId().equals(user.getUserId()))
                throw new Exception("User doesn't have enough permission to delete the comment");
            PostTable post = comment.getPostId();
            List<Integer> likeList = new ArrayList<>();
            List<LikesTable> likesFromDb = likesRepo.findByCommentId(comment);
            for(LikesTable l : likesFromDb){
                likesRepo.delete(l);
                likeList.add(l.getLikesId());
            }
            List<Integer> dislikeList = new ArrayList<>();
            List<DislikesTable> dislikesFromDb = dislikesRepo.findByCommentId(comment);
            for(DislikesTable d : dislikesFromDb){
                dislikesRepo.delete(d);
                likeList.add(d.getDislikesId());
            }
            commentRepo.delete(comment);
            DeleteCommentResponse response = new DeleteCommentResponse(comment.getUserId().getUserId(), comment.getUserId().getUsername(), post.getPostId(),
                    comment.getCommentsId(), likeList, dislikeList);
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to delete comment because: "+e);
        }
    }

    public UpdateCommentResponse updateComment(UpdateCommentRequest updateCommentRequest) throws Exception {
        try{
            UserTable user = checkExistence.checkUserExists(updateCommentRequest.getUserId());
            CommentsTable comment = checkExistence.checkCommentExists(updateCommentRequest.getCommentId());
            if(!comment.getUserId().getUserId().equals(user.getUserId()))
                throw new Exception("User doesn't have permission to update the comment");
            comment.setCommentText(updateCommentRequest.getCommentString());
            CommentsTable savedComment = commentRepo.save(comment);
            UpdateCommentResponse response = new UpdateCommentResponse(savedComment.getUserId().getUserId(), comment.getCommentsId(), comment.getUserId().getUsername(),
                    comment.getCommentText(), comment.getPostId().getPostId());
            return response;
        }catch (Exception e){
            log.error(e);
            throw new Exception("Unable to update comment because: "+e);
        }
    }
}
