package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.TaskDto;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.TaskService;

@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    @Operation(summary = "ADMIN/TEACHER lessonga task qushish")
    @PostMapping("/{lessonId}")
    public ResponseEntity<ApiResponse> saveTask(@RequestBody TaskDto taskDto, @PathVariable Integer lessonId) {
        ApiResponse apiResponse = taskService.saveTask(taskDto, lessonId);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @Operation(summary = "ADMIN, student va teacher lessonning barcha tasklarini kurish")
    @GetMapping("/getTaskByLesson/{lessonId}")
    public ResponseEntity<ApiResponse> getAllTasks(@PathVariable Integer lessonId, @CurrentUser User user) {
        ApiResponse allTasks = taskService.getAllTasks(user, lessonId);
        return ResponseEntity.ok(allTasks);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @Operation(summary = "ADMIN, student va teacher bitta taskni kurish")
    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse> getOneTasks(@PathVariable Integer taskId, @CurrentUser User user) {
        ApiResponse allTasks = taskService.getOneTask(taskId, user);
        return ResponseEntity.ok(allTasks);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    @Operation(summary = "ADMIN/TEACHER tasklarini update qilish")
    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse> updateTask(@PathVariable Integer taskId, @RequestBody TaskDto taskDto) {
        ApiResponse apiResponse = taskService.updateTask(taskDto, taskId);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN/TEACHER uzi qushgan tasklarni delete qilish")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable Integer taskId) {
        ApiResponse apiResponse = taskService.deleteTask(taskId);
        return ResponseEntity.ok(apiResponse);
    }
}
