package com.example.sfera_education.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleDTO {
    private Integer moduleId;
    private String name;
    private Integer categoryId;

    @Schema(hidden = true)
    private Integer lessonCount;
}
