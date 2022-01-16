package com.example.redditbackend.repository;

import com.example.redditbackend.entity.CommentsTable;
import com.example.redditbackend.entity.PostTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentsTableRepository extends JpaRepository<CommentsTable, Integer> {
    List<CommentsTable> findByPostId(PostTable postId);
    @Query(value = "select count(*) from comments_table ct where ct.post_id = ?1", nativeQuery = true)
    List<Object> findCountOfComments(Integer postId);
}
