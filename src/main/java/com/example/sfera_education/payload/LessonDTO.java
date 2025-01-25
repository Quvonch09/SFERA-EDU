package com.example.sfera_education.payload;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonDTO {

    @Schema(hidden = true)
    private Integer id;

    private String name;

    private String description;

    private String videoLink;

    private Integer videoTime;

    private Integer moduleId;

    private Long fileId;

    @Schema(hidden = true)
    private boolean userActive = false;

    @Schema(hidden = true)
    private boolean deleted = false;

    @Schema(hidden = true)
    private String moduleName;

    @Schema(hidden = true)
    private String categoryName;
    @Schema(hidden = true)
    private Integer categoryId;
}
