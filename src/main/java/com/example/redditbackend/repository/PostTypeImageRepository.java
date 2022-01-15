package com.example.redditbackend.repository;

import com.example.redditbackend.entity.PostTable;
import com.example.redditbackend.entity.PostTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTypeImageRepository extends JpaRepository<PostTypeImage, Integer> {
    PostTypeImage findByPostId(PostTable post);
}
