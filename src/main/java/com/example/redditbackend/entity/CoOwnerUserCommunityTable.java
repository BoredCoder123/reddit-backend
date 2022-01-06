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
@Table(name = "co_owner_user_community_table")
public class CoOwnerUserCommunityTable {
    @Id
    @GeneratedValue
    @Column(name = "co_owner_user_community_id")
    private Integer coOwnerUserCommunityId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserTable userId;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    private CommunityTable communityId;

    @Column(name = "become_co_owner_date", nullable = false)
    private Date becomeCoOwnerDate;

    @Column(name = "currently_active")
    private Boolean currentlyActive;

    @Column(name = "status_change_date")
    private Date statusChangeDate;
}
