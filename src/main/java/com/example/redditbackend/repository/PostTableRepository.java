package com.example.redditbackend.repository;

import com.example.redditbackend.entity.PostTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTableRepository extends JpaRepository<PostTable, Integer> {
    @Query(value = "select * from post_table pt where post_id not in " +
                "(select pt2.post_id from post_table pt2 order by pt2.likes desc, pt2.dislikes asc limit ?1) " +
            "order by pt.likes desc, pt.dislikes asc limit ?2", nativeQuery = true)
    List<PostTable> findTopViewOfAllTime(int start, int end);
}
