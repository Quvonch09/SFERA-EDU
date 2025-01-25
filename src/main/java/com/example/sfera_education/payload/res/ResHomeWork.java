package com.example.sfera_education.payload.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResHomeWork {

    private Long studentId;
    private String firstName;
    private String lastName;
    private String groupName;
    private Integer homeworkId;


}
