package com.example.sfera_education.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizCategorySettingsDTO {

    @Schema(hidden = true)
    private Integer id;

    private Integer countQuiz;

    private Integer durationTime;
}
