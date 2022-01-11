package com.example.redditbackend.repository;

import com.example.redditbackend.entity.PostTypeText;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTypeTextRepository extends JpaRepository<PostTypeText, Integer> {
}
