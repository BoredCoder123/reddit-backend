package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.NormalUserCommunityTable;
import com.example.redditbackend.entity.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalUserCommunityTableRepository extends JpaRepository<NormalUserCommunityTable, Integer> {
    public NormalUserCommunityTable findByUserIdAndCommunityId(UserTable userId, CommunityTable communityId);
}
