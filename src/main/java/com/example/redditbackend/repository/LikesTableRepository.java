package com.example.redditbackend.repository;

import com.example.redditbackend.entity.LikesTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesTableRepository extends JpaRepository<LikesTable, Integer> {
}
