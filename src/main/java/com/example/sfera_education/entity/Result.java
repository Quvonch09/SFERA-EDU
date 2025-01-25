package com.example.sfera_education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String categoryName;

    private Integer countAnswer;

    private Integer correctAnswer;

    private Integer duration;

    private int statusCode;

    private LocalDate createdAt;
}