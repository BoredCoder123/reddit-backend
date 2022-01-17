package com.example.redditbackend.repository;

import com.example.redditbackend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DislikesTableRepository extends JpaRepository<DislikesTable, Integer> {
    public DislikesTable findByPostIdAndUserId(PostTable postId, UserTable userId);
    public DislikesTable findByCommentIdAndUserId(CommentsTable commentId, UserTable userId);
    List<DislikesTable> findByPostId(PostTable post);

    List<DislikesTable> findByCommentId(CommentsTable comment);
}
