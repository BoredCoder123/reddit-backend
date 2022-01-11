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
@Table(name="post_type_video")
@Entity
public class PostTypeVideo {
    @Id
    @GeneratedValue
    @Column(name="video_id")
    private Integer videoId;

    @Column(name="video_title")
    private String videoTitle;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostTable postId;

    @Column(name="video_url")
    private String videoUrl;
}
