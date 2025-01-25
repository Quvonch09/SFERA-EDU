package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResResult {
    private int countResult;
    private int resultCategoryCount;
    private int passedResultCount;
}
