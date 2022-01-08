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
@Entity
@Table(name = "normal_user_community_table")
public class NormalUserCommunityTable {
    @Id
    @GeneratedValue
    @Column(name = "normal_user_community_id")
    private Integer normalUserCommunityId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserTable userId;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    private CommunityTable communityId;

    @Column(name = "join_date")
    private Date joinDate;

    @Column(name = "is_user_banned", nullable = false)
    private Boolean isUserBanned;
}
