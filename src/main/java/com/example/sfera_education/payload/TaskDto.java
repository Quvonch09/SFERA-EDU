package com.example.sfera_education.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {

    @Schema(hidden = true)
    private Integer id;

    private String name;

    private String description;

    @Schema(hidden = true)
    private Integer lessonId;

    private Long fileId;

    @Schema(hidden = true)
    private boolean send;
}
