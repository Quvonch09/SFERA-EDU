package com.example.sfera_education.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "group_days",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "days_id")}
    )
    private List<DayOfWeek> days;

    @ManyToOne
    private User teacher;

    @OneToMany
    private List<User> students;

    @ManyToMany
    @JoinTable(
            name = "groups_delete_students",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "delete_student_id")}
    )
    private List<User> deleteStudents;

    private boolean active;

    private LocalDate startDate;

    private String startTime;

    private String endTime;
}
