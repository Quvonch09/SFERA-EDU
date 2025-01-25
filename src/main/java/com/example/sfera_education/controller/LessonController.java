package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.LessonDTO;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.LessonService;


@CrossOrigin
@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
public class LessonController {


    private final LessonService lessonService;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    @Operation(summary = "ADMIN/TEACHER lesson save qilish", description = "file bu admin yoki teacher yuklashi kerak bolgan prezentatsiya")
    @PostMapping
    public ResponseEntity<ApiResponse> saveLesson(@RequestBody LessonDTO lesson) {
        return ResponseEntity.ok(lessonService.saveLesson(lesson));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT')")
    @Operation(summary = "Bitta lessonni korish 3ta sayt uchun ham! hammaga")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLessons(@PathVariable Integer id, @CurrentUser User user) {
        return ResponseEntity.ok(lessonService.getOneLesson(id, user));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_USER')")
    @Operation(summary = "Hamma uchun modul id bn lessonni korishi")
    @GetMapping("/list/edu/{moduleId}")
    public ResponseEntity<ApiResponse> getLessonList(@PathVariable Integer moduleId, @CurrentUser User user) {
        return ResponseEntity.ok(lessonService.getAllLessonsEdu(moduleId, user));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    @Operation(summary = "Admin yoki teacher lessonni ozgartirishi")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLesson(@PathVariable Integer id, @RequestBody LessonDTO lesson) {
        return ResponseEntity.ok(lessonService.updateLesson(id, lesson));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteLesson(@PathVariable Integer id) {
        return ResponseEntity.ok(lessonService.deleteLesson(id));
    }


    @GetMapping("/online/start/{categoryId}/course")
    public ResponseEntity<ApiResponse> startCourse(@PathVariable("categoryId") Integer categoryId,
                                                   @CurrentUser User user) {
        ApiResponse apiResponse = lessonService.startCourse(categoryId, user);
        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping("/online/{lessonId}")
    public ResponseEntity<ApiResponse> getOneLesson(@PathVariable("lessonId") Integer lessonId) {
        ApiResponse apiResponse = lessonService.getLesson(lessonId);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/online/courses")
    public ResponseEntity<ApiResponse> getUserCourses(@CurrentUser User user) {
        ApiResponse apiResponse = lessonService.getUserCourses(user);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "USER, STUDENT categoryId buyicha lessonlarni kurish")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_USER')")
    @GetMapping("/listOnlineLesson/{categoryId}")
    public ResponseEntity<ApiResponse> getOnlineLessonsByCategory(@PathVariable Integer categoryId, @CurrentUser User user) {
        ApiResponse lessonByCategoryId = lessonService.getLessonByCategoryId(categoryId, user);
        return ResponseEntity.ok(lessonByCategoryId);
    }


    @Operation(summary = "Teacher/admin search qilishi")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchLesson(@RequestParam(value = "name", required = false) String name,
                                                    @RequestParam(value = "teacherId", required = false) Long teacherId,
                                                    @RequestParam(value = "moduleId", required = false) Integer moduleId,
                                                    @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                    @RequestParam(value = "categoryEnum") CategoryEnum categoryEnum,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(lessonService.searchLesson(name, teacherId, moduleId, categoryId, categoryEnum, page, size));
    }

}
