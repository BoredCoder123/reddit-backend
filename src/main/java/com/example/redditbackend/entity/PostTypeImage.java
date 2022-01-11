package com.example.redditbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="post_type_image")
@Entity
public class PostTypeImage {
    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Integer imageId;

    @Column(name="image_title")
    private String imageTitle;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostTable postId;

    @Column(name="image_url")
    private String url;

    @Lob
    @Column(name="image_source")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] imageSource;
}
