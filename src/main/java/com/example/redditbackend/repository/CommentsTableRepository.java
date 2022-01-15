package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CommentsTable;
import com.example.redditbackend.entity.PostTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsTableRepository extends JpaRepository<CommentsTable, Integer> {
    List<CommentsTable> findByPostId(PostTable postId);
}
