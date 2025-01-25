package com.example.sfera_education.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticDTO {

    private Integer teacherCount;
    private Integer studentCount;
    private Integer categoryCount;
    private Integer groupCount;
}
