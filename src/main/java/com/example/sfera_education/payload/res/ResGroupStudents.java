package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResGroupStudents {
    private Long studentId;
    private String fullName;
    private boolean active;
}
