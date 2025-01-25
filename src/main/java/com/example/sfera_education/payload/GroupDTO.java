package com.example.sfera_education.payload;

import com.example.sfera_education.payload.res.ResGroupStudents;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDTO {

    private Integer id;
    private String name;
    private Long teacherId;
    private String teacherName;
    private Integer categoryId;
    private List<String> daysName;
    private List<ResGroupStudents> students;
    private LocalDate startDate;
    private String startTime;
    private String endTime;
    private boolean active;
}
