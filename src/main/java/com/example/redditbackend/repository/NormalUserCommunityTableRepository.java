package com.example.redditbackend.repository;

import com.example.redditbackend.entity.NormalUserCommunityTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalUserCommunityTableRepository extends JpaRepository<NormalUserCommunityTable, Integer> {
}
