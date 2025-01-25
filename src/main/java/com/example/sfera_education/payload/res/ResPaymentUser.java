package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResPaymentUser {
    private String lastName;
    private String firstName;
    private Double payment;

}
