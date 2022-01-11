package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CommunityTable;
import com.example.redditbackend.entity.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityTableRepository extends JpaRepository<CommunityTable, Integer> {
    public CommunityTable findByCommunityName(String communityName);
    public List<CommunityTable> findByCurrentOwner(UserTable userTable);
    public List<CommunityTable> findByCreatorId(UserTable userTable);
    public CommunityTable findByCurrentOwnerAndCommunityId(UserTable currentOwner, Integer communityId);
    @Query(value = "select community_id, community_name, community_description from community_table ct where ct.community_id not in \n" +
            "(select distinct(ct2.community_id) from community_table ct2 join normal_user_community_table nuct on ct2.community_id=nuct.community_id \n" +
            "where nuct.user_id=?1)", nativeQuery = true)
    public List<Object> findCommunitiesNotJoined(Integer userId);
    @Query(value = "select community_id, community_name, community_description from community_table ct where ct.community_id in \n" +
            "(select distinct(ct2.community_id) from community_table ct2 join normal_user_community_table nuct on ct2.community_id=nuct.community_id \n" +
            "where nuct.user_id=?1)", nativeQuery = true)
    public List<Object> findCommunitiesJoined(Integer userId);
    @Query(value = "select rules from community_table_rules where community_table_community_id=?1", nativeQuery = true)
    public List<Object> fetchRules(Integer communityId);
}
