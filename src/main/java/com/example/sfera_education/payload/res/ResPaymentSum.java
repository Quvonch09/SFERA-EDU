package com.example.sfera_education.payload.res;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResPaymentSum {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalSum;
}
