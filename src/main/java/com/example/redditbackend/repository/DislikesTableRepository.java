package com.example.redditbackend.repository;

import com.example.redditbackend.entity.DislikesTable;
import com.example.redditbackend.entity.PostTable;
import com.example.redditbackend.entity.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DislikesTableRepository extends JpaRepository<DislikesTable, Integer> {
    public DislikesTable findByPostIdAndUserId(PostTable postId, UserTable userId);
}
