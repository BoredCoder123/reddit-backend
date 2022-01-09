package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.ModUserCommunityTable;
import com.example.redditbackend.entity.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModUserCommunityTableRepository extends JpaRepository<ModUserCommunityTable, Integer> {
    public ModUserCommunityTable findByCommunityTableAndUserId(CommunityTable communityTable, UserTable userId);
}
