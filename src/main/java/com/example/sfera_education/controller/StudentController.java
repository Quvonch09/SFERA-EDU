package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.StudentService;

@CrossOrigin
@Tag(name = "Student Controller")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;


    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Operation(summary = "Studenti infosi")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse> getStatisticStudent(@CurrentUser User user) {
        ApiResponse apiResponse = studentService.getCountAllAndAvailableLessonsAndScoreAndRate(user.getId());
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Operation(summary = "Studentlarni reytingi")
    @GetMapping("/rating")
    public ResponseEntity<ApiResponse> getRatingStudents(@CurrentUser User user) {
        ApiResponse ratingStudents = studentService.getRatingStudents(user);
        return ResponseEntity.ok(ratingStudents);
    }


    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Operation(summary = "Studentlarni o'zi oqiydiga categoryni chiqarib berishi!")
    @GetMapping("/course")
    public ResponseEntity<ApiResponse> getStudentCategory(@CurrentUser User user) {
        ApiResponse ratingStudents = studentService.getCategoryStudent(user);
        return ResponseEntity.ok(ratingStudents);
    }


    @GetMapping("/userDashboardCount")
    @Operation(summary = "Online userlarning dashboard counti")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> getUserDashboardCount(@CurrentUser User user) {
        ApiResponse countUserDashboard = studentService.getCountUserDashboard(user);
        return ResponseEntity.ok(countUserDashboard);
    }


}
