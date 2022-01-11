package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CommentsTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsTableRepository extends JpaRepository<CommentsTable, Integer> {
}
