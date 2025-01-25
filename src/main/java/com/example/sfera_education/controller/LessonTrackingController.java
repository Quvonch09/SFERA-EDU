package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.LessonTrackingDTO;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.LessonTrackingService;

@CrossOrigin
@RestController
@RequestMapping("/lesson/tracking")
@RequiredArgsConstructor
public class LessonTrackingController {

    private final LessonTrackingService lessonTrackingService;


    @Operation(summary = "Teacher lessonga o'ziga tegishli guruhni biriktirishni saqlashi")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping
    public HttpEntity<ApiResponse> saveLessonTracking(@RequestBody LessonTrackingDTO reqLessonTracking, @CurrentUser User user) {
        ApiResponse apiResponse = lessonTrackingService.saveLessonTracking(reqLessonTracking, user);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Teacher lessonga o'ziga tegishli guruhni biriktirishni o'zgartirishi")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PutMapping("/update/{id}")
    public HttpEntity<ApiResponse> updateLessonTracking(@PathVariable("id") Integer id, @RequestBody LessonTrackingDTO reqLessonTracking, @CurrentUser User user) {
        ApiResponse apiResponse = lessonTrackingService.updateLessonTracking(id, reqLessonTracking, user);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Teacher lessonga guruhni biriktirishni o'ziga tegishli bittasini ko'rishi")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/{id}")
    public HttpEntity<ApiResponse> getLessonTracking(@PathVariable("id") Integer id, @CurrentUser User user) {
        ApiResponse apiResponse = lessonTrackingService.getLessonTracking(id, user);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Teacher lessonga guruhni biriktirishni o'ziga tegishli hammasini ko'rishi")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping
    public HttpEntity<ApiResponse> getLessonTrackingList(@CurrentUser User user) {
        ApiResponse apiResponse = lessonTrackingService.getTeacherByLessonTrackingList(user);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Teacher lessonga guruhni biriktirishni o'chirishi")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @DeleteMapping("/{id}")
    public HttpEntity<ApiResponse> deleteLessonTracking(@PathVariable("id") Integer id) {
        ApiResponse apiResponse = lessonTrackingService.deleteLessonTracking(id);
        return ResponseEntity.ok(apiResponse);
    }


}
