package com.example.sfera_education.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class HomeWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Task task;

    @ManyToOne
    private User student;

    private String solution;

    private Integer score;

    private LocalDate dueDate;

    @OneToOne
    private File file;

    private boolean checked;
}