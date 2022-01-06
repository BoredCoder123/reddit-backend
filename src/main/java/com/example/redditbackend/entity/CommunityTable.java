package com.example.redditbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "community_table")
@Entity
public class CommunityTable {
    @Id
    @GeneratedValue
    @Column(name = "community_id")
    private Integer communityId;

    @Column(name = "community_name", unique = true, nullable = false)
    private String communityName;

    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @ElementCollection
    @OrderColumn
    @Column(name = "rules")
    private List<String> rules;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private UserTable creatorId;

    @ManyToOne
    @JoinColumn(name = "current_owner", nullable = false)
    private UserTable currentOwner;

    @OneToMany(mappedBy = "communityId")
    @JsonIgnore
    private List<NormalUserCommunityTable> listOfCommunities;
}
