package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.QuizCategorySettingsDTO;
import com.example.sfera_education.service.QuizCategorySettingsService;

@Controller
@CrossOrigin
@RestController
@RequestMapping("/quiz-category/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class QuizCategorySettingsController {

    private final QuizCategorySettingsService settingsService;

    @Operation(summary = "Admin settingsni korishi")
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> getSettings(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(settingsService.getQuizCategorySettings(categoryId));
    }

    @Operation(summary = "Admin settingsni o'zgartirishi")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> updateSettings(@PathVariable Integer categoryId, @RequestBody QuizCategorySettingsDTO quizCategorySettings) {
        return ResponseEntity.ok(settingsService.updateQuizCategorySettings(categoryId, quizCategorySettings));
    }
}
