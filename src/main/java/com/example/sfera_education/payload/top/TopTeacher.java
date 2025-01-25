package com.example.sfera_education.payload.top;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopTeacher {

    private Long id;

    private String fullName;

    private String phoneNumber;

    private Double scoreMonth;

}
