package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CoOwnerUserCommunityTable;
import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoOwnerUserCommunityTableRepository extends JpaRepository<CoOwnerUserCommunityTable, Integer> {
    CoOwnerUserCommunityTable findByUserIdAndCommunityId(UserTable userId, CommunityTable communityId);
}
