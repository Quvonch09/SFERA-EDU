package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResCategory {

    private String categoryName;
    private Double percentage;
}
