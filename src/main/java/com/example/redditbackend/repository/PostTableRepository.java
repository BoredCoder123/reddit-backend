package com.example.redditbackend.repository;

import com.example.redditbackend.entity.PostTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTableRepository extends JpaRepository<PostTable, Integer> {
}
