package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResCountQuiz {

    private int categoryCount;
    private int resultCount;
    private int userCount;
    private int questionCount;
    private int badResultsCount;
    private int goodResultsCount;
    private int superResultsCount;
    private int todayResultsCount;

}
