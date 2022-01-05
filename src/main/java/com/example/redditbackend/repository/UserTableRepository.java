package com.example.redditbackend.repository;

import com.example.redditbackend.entity.UserTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTableRepository extends JpaRepository<UserTable, Integer> {
    public UserTable findByEmail(String email);
    public UserTable findByUsername(String username);
}
