package com.example.sfera_education.payload.res;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResGroup {

    private String name;
    private Integer categoryId;
    private List<Integer> daysWeekIds;
    private Long teacherId;
    private LocalDate startDate;
    private String startTime;
    private String endTime;
}
