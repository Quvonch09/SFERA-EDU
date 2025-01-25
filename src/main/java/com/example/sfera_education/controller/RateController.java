package com.example.sfera_education.controller;

import com.example.sfera_education.service.RateService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.security.CurrentUser;


@CrossOrigin
@RestController
@RequestMapping("/rate")
@RequiredArgsConstructor
public class RateController {


    private final RateService rateService;

    @Operation(summary = "Edu Admin uchun rate panelidagi group larning yillik statistikasi")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/groupByYearly")
    public ResponseEntity<ApiResponse> groupByYearly() {
        ApiResponse groupByYearlyStatistic = rateService.getGroupByYearlyStatistic();
        return ResponseEntity.ok(groupByYearlyStatistic);
    }

    @Operation(summary = "Edu Admin uchun rate panelidagi studentlar page")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/studentsRate")
    public ResponseEntity<ApiResponse> getStudentsRate(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Integer groupId,
                                                       @RequestParam(required = false) Integer categoryId,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        ApiResponse apiResponse = rateService.getStudents(keyword, groupId, categoryId, page, size);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "EDU Teacher dashboardagi studentlar  filteri ")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/studentsRate-teacher")
    public ResponseEntity<ApiResponse> getStatistic(@CurrentUser User user,
                                                    @RequestParam(required = false) Integer groupId,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        ApiResponse studentsForTeacher = rateService.getStudentsForTeacher(user, groupId, page, size);
        return ResponseEntity.ok(studentsForTeacher);
    }


}
