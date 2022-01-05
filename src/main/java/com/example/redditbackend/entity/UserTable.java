package com.example.redditbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_table")
@Entity
public class UserTable {
    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "join_date", nullable = false)
    private Date joinDate;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "last_logged_in", nullable = false)
    private Date lastLoggedIn;
}
