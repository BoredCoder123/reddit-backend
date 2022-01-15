package com.example.redditbackend.repository;

import com.example.redditbackend.entity.PostTable;
import com.example.redditbackend.entity.PostTypeVideo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTypeVideoRepository extends JpaRepository<PostTypeVideo, Integer> {
    PostTypeVideo findByPostId(PostTable post);
}
