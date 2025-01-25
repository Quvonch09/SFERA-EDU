package com.example.sfera_education.entity;

import com.example.sfera_education.entity.enums.CategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    private boolean active;

    @Enumerated(EnumType.STRING)
    private CategoryEnum categoryEnum;

    @OneToOne
    private File file;
}


