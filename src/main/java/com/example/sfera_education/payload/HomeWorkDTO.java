package com.example.sfera_education.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeWorkDTO {

    @Schema(hidden = true)
    private Integer id;

    private Integer taskId;

    @Schema(hidden = true)
    private Long studentId;

    private String solution;

    @Schema(hidden = true)
    private Integer score;

    private Long fileId;

    @Schema(hidden = true)
    private boolean checked;
}
