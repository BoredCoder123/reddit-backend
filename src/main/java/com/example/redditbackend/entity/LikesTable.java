package com.example.redditbackend.entity;

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
@Table(name = "likes_table")
public class LikesTable {
    @Id
    @GeneratedValue
    @Column(name = "likes_id")
    private Integer likesId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostTable postId;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentsTable commentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserTable userId;

    @Column(name="date_added")
    private Date dateAdded;
}
