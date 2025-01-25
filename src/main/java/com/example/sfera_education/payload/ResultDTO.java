package com.example.sfera_education.payload;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultDTO {

    private Long id;

    private Long userId;

    private String userName;

    private String categoryName;

    private Integer countAnswer;

    private Integer correctAnswer;

    private Integer duration;

    private String status;

    private LocalDate createdAt;
}
