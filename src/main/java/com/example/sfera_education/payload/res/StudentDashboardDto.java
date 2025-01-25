package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDashboardDto {

    private Integer categoryCount;
    private Integer wonLessonCount;
    private Integer lessonCount;
}
