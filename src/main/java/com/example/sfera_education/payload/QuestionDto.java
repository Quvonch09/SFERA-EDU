package com.example.sfera_education.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionDto {
    @Schema(hidden = true)
    private Integer id;

    private String name;

    @Schema(hidden = true)
    private Integer categoryId;

    @Schema(hidden = true)
    private String categoryName;

    @Schema(hidden = true)
    private Integer lessonId;

    @Schema(hidden = true)
    private String lessonName;

    private List<OptionDto> optionDto;

}
