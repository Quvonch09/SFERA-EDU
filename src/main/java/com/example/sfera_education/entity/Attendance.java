package com.example.sfera_education.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User student;
    private boolean isAttendance;
    private LocalDate date;
    private Integer groupId;
    @ManyToOne
    private User teacher;

    private LocalDateTime createdAt;
    @ManyToOne
    private User updatedBy;
    private LocalDateTime updatedAt;
}
