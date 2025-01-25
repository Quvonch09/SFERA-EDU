package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResRateStudent {

    private String fullName;
    private String categoryName;
    private String groupName;
    private int rate;
    private int score;
}
