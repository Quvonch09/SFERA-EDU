package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineStatisticDto {
    private Integer categoryCount;
    private Integer moduleCount;
    private Integer lessonCount;
    private Long studentCount;
}
