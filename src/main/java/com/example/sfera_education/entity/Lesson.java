package com.example.sfera_education.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String videoLink;

    private Integer videoTime;

    @OneToOne
    private QuizCategorySettings quizCategorySettings;

    @ManyToOne
    private Module module;

    @OneToOne
    private File file;

    private boolean deleted = false;


}
