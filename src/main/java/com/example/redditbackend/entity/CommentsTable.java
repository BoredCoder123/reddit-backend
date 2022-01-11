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
@Table(name = "comments_table")
public class CommentsTable {
    @Id
    @GeneratedValue
    @Column(name = "comments_id")
    private Integer commentsId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostTable postId;

    @ManyToOne
    @JoinColumn(name = "comment_linked_to")
    private CommentsTable commentLinkedTo;

    @Column(name="comment_text", columnDefinition = "text")
    private String commentText;

    @Column(name="date_added")
    private Date dateAdded;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserTable userId;

    @Column(name="likes")
    private Integer likes;

    @Column(name="dislikes")
    private Integer dislikes;

    @Column(name="is_comment_banned")
    private Boolean isCommentBanned;

    @Column(name="ban_reason", columnDefinition = "text")
    private String banReason;
}
