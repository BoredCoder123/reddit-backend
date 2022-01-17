package com.example.redditbackend.utility;

import com.example.redditbackend.entity.*;
import com.example.redditbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckExistence {
    @Autowired
    private UserTableRepository userRepo;

    @Autowired
    private CommunityTableRepository communityRepo;

    @Autowired
    private NormalUserCommunityTableRepository normalRepo;

    @Autowired
    private PostTableRepository postRepo;

    @Autowired
    private CommentsTableRepository commentRepo;

    @Autowired
    private ModUserCommunityTableRepository modRepo;

    @Autowired
    private CoOwnerUserCommunityTableRepository coRepo;

    public UserTable postTest(){
        return userRepo.findByUsername("test1");
    }

    public UserTable checkUserExists(Integer userId) throws Exception{
        Optional<UserTable> checkUser = userRepo.findById(userId);
        if(!checkUser.isPresent())
            throw new Exception("Unable to find user");
        return checkUser.get();
    }

    public CommunityTable checkCommExists(Integer communityId) throws Exception{
        Optional<CommunityTable> checkComm = communityRepo.findById(communityId);
        if(!checkComm.isPresent())
            throw new Exception("Unable to find community");
        return checkComm.get();
    }

    public NormalUserCommunityTable checkUserInComm(UserTable user, CommunityTable comm) throws Exception{
        NormalUserCommunityTable checkUser = normalRepo.findByUserIdAndCommunityId(user, comm);
        if(checkUser == null)
            throw new Exception("Unable to find user in community");
        if(checkUser.getIsUserBanned())
            throw new Exception("User is banned in the community");
        return checkUser;
    }

    public PostTable checkPostsExists(Integer postId) throws Exception{
        Optional<PostTable> checkPost = postRepo.findById(postId);
        if(!checkPost.isPresent())
            throw new Exception("Unable to find post");
        return checkPost.get();
    }

    public CommentsTable checkCommentExists(Integer commentId) throws Exception{
        Optional<CommentsTable> checkComment = commentRepo.findById(commentId);
        if(!checkComment.isPresent())
            throw new Exception("Unable to find comment");
        return checkComment.get();
    }

    public Boolean checkIfUserIsModCoOrOwner(UserTable user, CommunityTable community){
        if(community.getCurrentOwner().getUserId().equals(user.getUserId()))
            return true;
        ModUserCommunityTable currentMod = modRepo.findByCommunityTableAndUserId(community, user);
        if(currentMod != null)
            return true;
        CoOwnerUserCommunityTable currentCo = coRepo.findByUserIdAndCommunityId(user, community);
        return currentCo != null;
    }
}
