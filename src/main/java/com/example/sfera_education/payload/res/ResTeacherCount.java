package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResTeacherCount {
    private Integer groupCount;
    private Integer studentCount;
    private Integer teacherCount;
}
