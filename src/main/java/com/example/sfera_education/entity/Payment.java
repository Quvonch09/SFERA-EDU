package com.example.sfera_education.entity;

import com.example.sfera_education.entity.enums.PayType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double paySum;
    private LocalDate payDate;
    @Enumerated(EnumType.STRING)
    private PayType payType;
    @ManyToOne
    private User student;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
