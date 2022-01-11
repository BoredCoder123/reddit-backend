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
@Table(name="post_type_text")
@Entity
public class PostTypeText {
    @Id
    @GeneratedValue
    @Column(name="text_id")
    private Integer textId;

    @Column(name="text_title")
    private String textTitle;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostTable postId;

    @Column(name="text_description", columnDefinition = "text")
    private String textDescription;
}
