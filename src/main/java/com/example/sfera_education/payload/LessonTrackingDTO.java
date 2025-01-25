package com.example.sfera_education.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonTrackingDTO {

    @Schema(hidden = true)
    private Integer id;

    private Integer groupId;

    private Integer lessonId;

    private boolean active;

    @Schema(hidden = true)
    private String lessonName;

    @Schema(hidden = true)
    private String groupName;

}
