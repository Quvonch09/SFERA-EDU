package com.example.sfera_education.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String title;

    @Column(columnDefinition = "text")
    private String content;

    private LocalDateTime createdAt;

    private boolean read;

    @ManyToOne
    private File file;

    @ManyToOne
    private User user;

    private boolean contact;

    @ManyToOne
    private User registrant;


}
