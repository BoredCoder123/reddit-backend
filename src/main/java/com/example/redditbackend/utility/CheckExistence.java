package com.example.redditbackend.utility;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.NormalUserCommunityTable;
import com.example.redditbackend.entity.UserTable;
import com.example.redditbackend.repository.CommunityTableRepository;
import com.example.redditbackend.repository.NormalUserCommunityTableRepository;
import com.example.redditbackend.repository.UserTableRepository;
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
}
