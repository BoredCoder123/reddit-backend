package com.example.redditbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Table(name = "mod_user_community_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ModUserCommunityTable {
    @Id
    @GeneratedValue
    @Column(name = "mod_user_id")
    private Integer modUserId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserTable userId;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    private CommunityTable communityTable;

    @Column(name = "become_mod_date", nullable = false)
    private Date becomeModDate;

    @ManyToOne
    @JoinColumn(name = "promoted_by", nullable = false)
    private CoOwnerUserCommunityTable promotedBy;

    @Column(name = "currently_active_mod", nullable = false)
    private Boolean currentlyActiveMod;

    @Column(name = "status_change")
    private Date statusChange;
}
