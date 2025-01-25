package com.example.sfera_education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Lesson lesson;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Option> options;
}