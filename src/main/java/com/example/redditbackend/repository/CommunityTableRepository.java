package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityTableRepository extends JpaRepository<CommunityTable, Integer> {
    public CommunityTable findByCommunityName(String communityName);
    public List<CommunityTable> findByCurrentOwner(UserTable userTable);
    public List<CommunityTable> findByCreatorId(UserTable userTable);
    public CommunityTable findByCurrentOwnerAndCommunityId(UserTable currentOwner, CommunityTable communityId);
}
