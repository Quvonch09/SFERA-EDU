package com.example.sfera_education.payload;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendDto {
    private Long id;
    private Boolean attendance;
    private LocalDate date;
}
