package com.example.sfera_education.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceDto {
    @Schema(hidden = true)
    private Long id;
    @Schema(hidden = true)
    private String studentName;
    @Schema(hidden = true)
    private String studentLastName;
    private Long studentId;
    private boolean attendance;
    private LocalDate date;


}
