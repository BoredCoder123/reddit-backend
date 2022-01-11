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
@Table(name = "dislikes_table")
public class DislikesTable {
    @Id
    @GeneratedValue
    @Column(name = "dislikes_id")
    private Integer dislikesId;

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
