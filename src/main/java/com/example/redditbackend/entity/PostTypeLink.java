package com.example.redditbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="post_type_link")
@Entity
public class PostTypeLink {
    @Id
    @GeneratedValue
    @Column(name="link_id")
    private Integer linkId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostTable postId;

    @Column(name="link_title")
    private String linkTitle;

    @Column(name="link_url")
    private String linkUrl;
}
