package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CommunityTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityTableRepository extends JpaRepository<CommunityTable, Integer> {
    public CommunityTable findByCommunityName(String communityName);
}
