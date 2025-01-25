package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.AttendanceDto;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.AttendanceService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Admin yoki Teacher studentlarni yoqlamasini qilish",
            description = "reason - bu yerda sababli yoki sabsiz ekanligini bildiradi, true=sababli, false=sababsiz\n" +
                    "studentId - bu esa qaysi studentni yoqlamasi uchun,\n" +
                    "date - default holatda today bo'lishi kerak, agarda teacher yoki admin yoqlama esdan chikib ketsa kechagi sanani kiritishi mumkin")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> create(@RequestBody List<AttendanceDto> attendanceDtos,
                                              @CurrentUser User user) {
        ApiResponse apiResponse = attendanceService.create(attendanceDtos, user);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Operation(summary = "student o'zini davomatini ko'rishi")
    @GetMapping("/myAttendance")
    public ResponseEntity<ApiResponse> getAttendanceByStudent(@CurrentUser User user, @RequestParam int month) {
        ApiResponse attendanceByStudent = attendanceService.getAttendanceByStudent(user, month);
        return ResponseEntity.ok(attendanceByStudent);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Admin yoki Teacher Studentlarni yo'qlamasini guruh boyicha ko'rish")
    @GetMapping("/by-group")
    public ResponseEntity<ApiResponse> getAttendanceByGroup(@RequestParam Integer groupId,
                                                            @RequestParam int year,
                                                            @RequestParam int month) {
        ApiResponse attendanceByGroupId = attendanceService.getAttendanceByGroupId(groupId, year, month);
        return ResponseEntity.ok(attendanceByGroupId);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Admin yoki Teacher studenti davomatini update qilish",
            description = "Bu narsa Admin yoki Teacher Boshqa studentga adashib yoq " +
                    "qilib qoysa uni update qilish uchun yoki bo'lmasa uni sababli qilib qoyish")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@RequestBody AttendanceDto attendanceDto,
                                              @PathVariable("id") Long id,
                                              @CurrentUser User user) {
        ApiResponse apiResponse = attendanceService.updateAttendance(attendanceDto, id, user);
        return ResponseEntity.ok(apiResponse);
    }


}
