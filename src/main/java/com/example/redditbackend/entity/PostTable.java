package com.example.redditbackend.entity;

import com.example.redditbackend.utility.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_table")
public class PostTable {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne
    @JoinColumn(name = "user_posted")
    private UserTable userPosted;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private CommunityTable communityId;

    @Column(name = "post_date")
    private Date postDate;

    @Column(name="post_type")
    private PostType postType;

    @Column(name="likes")
    private Integer likes;

    @Column(name="dislikes")
    private Integer dislikes;

    @Column(name="is_post_banned")
    private Boolean isPostBanned;

    @Column(name="ban_reason")
    private String banReason;

    public PostTable(UserTable userPosted, CommunityTable communityId, Date postDate, PostType postType, Integer likes, Integer dislikes, Boolean isPostBanned) {
        this.userPosted = userPosted;
        this.communityId = communityId;
        this.postDate = postDate;
        this.postType = postType;
        this.likes = likes;
        this.dislikes = dislikes;
        this.isPostBanned = isPostBanned;
    }
}
