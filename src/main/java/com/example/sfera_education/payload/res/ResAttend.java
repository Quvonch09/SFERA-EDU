package com.example.sfera_education.payload.res;

import com.example.sfera_education.payload.AttendDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResAttend {

    private Long studentId;
    @Schema(hidden = true)
    private String studentName;
    @Schema(hidden = true)
    private String studentLastName;
    private List<AttendDto> attendDtoList;
}
