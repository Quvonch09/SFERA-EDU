package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResLesson {
    private String categoryName;
    private String moduleName;
    private String lessonName;

}
