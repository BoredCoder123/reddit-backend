package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CommentsTable;
import com.example.redditbackend.entity.LikesTable;
import com.example.redditbackend.entity.PostTable;
import com.example.redditbackend.entity.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesTableRepository extends JpaRepository<LikesTable, Integer> {
    public LikesTable findByPostIdAndUserId(PostTable postId, UserTable userId);
    public LikesTable findByCommentIdAndUserId(CommentsTable commentId, UserTable userId);
}
