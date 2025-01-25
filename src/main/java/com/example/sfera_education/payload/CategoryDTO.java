package com.example.sfera_education.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    @Schema(hidden = true)
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Schema(hidden = true)
    private String categoryEnum;

    @Schema(hidden = true)
    private boolean active;

    @Schema(hidden = true)
    private Integer countQuiz;

    @Schema(hidden = true)
    private Integer durationTime;

    private Long fileId;

    @Schema(hidden = true)
    private Integer moduleCount;
}
