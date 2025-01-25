package com.example.sfera_education.payload;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private Long paymentId;
    private Double paySum;
    private LocalDate payDate;
    private String paymentType;
    private String userName;
    private Long userId;
    private Integer userGroupId;
    private LocalDateTime createdAt;
}
