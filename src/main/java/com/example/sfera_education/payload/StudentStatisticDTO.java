package com.example.sfera_education.payload;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentStatisticDTO {

    private Integer availableLessons;
    private Integer countAllLessons;
    private Integer score;
    private Integer ratingStudent;
    private Integer countRatingStudents;
}
