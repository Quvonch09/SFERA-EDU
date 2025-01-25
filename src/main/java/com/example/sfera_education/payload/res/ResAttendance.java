package com.example.sfera_education.payload.res;

import com.example.sfera_education.payload.AttendanceDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResAttendance {
    private List<AttendanceDto> attendanceDtos;
    private List<LocalDate> days;
}
