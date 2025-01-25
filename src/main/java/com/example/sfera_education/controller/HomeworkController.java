package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.HomeWorkDTO;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.HomeWorkService;

@CrossOrigin
@RestController
@RequestMapping("/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeWorkService homeWorkService;

    @Operation(summary = "Student uchun")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveHomework(@RequestBody HomeWorkDTO homeWork,
                                                    @CurrentUser User user) {
        return ResponseEntity.ok(homeWorkService.saveHomeWork(homeWork, user));
    }


    @Operation(summary = "Student ozini hamma homeworklarini koradi, " +
            "Teacher vazifasi tekshirilmagan o'quvchilari listini")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/list/all")
    public ResponseEntity<ApiResponse> listHomework(@CurrentUser User user) {
        return ResponseEntity.ok(homeWorkService.getHomeWork(user));
    }


    @Operation(summary = "Teacher bitta studentni tekshirilmagan vazifalarini ko'rishi uchun")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/list/{studentId}")
    public ResponseEntity<ApiResponse> listHomeworkByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(homeWorkService.getAllHomeworkByStudent(studentId));
    }


    @Operation(summary = "Student yoki teacher bitta homeworkni korishi")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getHomeworkById(@PathVariable Integer id) {
        return ResponseEntity.ok(homeWorkService.getOneHomeWork(id));
    }


    @Operation(summary = "Teacher homeworkga ball berishi")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping("/update/score/{id}")
    public ResponseEntity<ApiResponse> updateHomework(@CurrentUser User user,
                                                      @PathVariable Integer id,
                                                      @RequestParam Integer score) {
        return ResponseEntity.ok(homeWorkService.updateScore(user, id, score));
    }

    @Operation(summary = "Teacher uchun barcha tekshirilgan uyga vazifalar filter bo'yicha")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/homework-filter")
    public ResponseEntity<ApiResponse> getAllHomework(@CurrentUser User user,
                                                      @RequestParam(required = false) String studentFIO,
                                                      @RequestParam(required = false) Integer groupId,
                                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        ApiResponse allHomeWork = homeWorkService.getAllHomeWork(user, studentFIO, groupId, page, size);
        return ResponseEntity.ok(allHomeWork);
    }
}
