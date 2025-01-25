package com.example.sfera_education.payload.res;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResPayment {

    private Double paySum;
    private LocalDate payDate;
    private Long userId;
}
