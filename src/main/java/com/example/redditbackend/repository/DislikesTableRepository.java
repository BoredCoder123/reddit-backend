package com.example.redditbackend.repository;

import com.example.redditbackend.entity.DislikesTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DislikesTableRepository extends JpaRepository<DislikesTable, Integer> {
}
