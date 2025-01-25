package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.res.AnswerDTO;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.QuizService;

import java.util.List;

@Controller
@CrossOrigin
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_STUDENT')")
    @Operation(summary = "Student yoki User ")
    @GetMapping("/start/{categoryId}")
    public ResponseEntity<ApiResponse> startQuiz(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(quizService.startQuiz(categoryId));
    }


    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_STUDENT')")
    @Operation(summary = "Student yoki User ")
    @PostMapping("/pass/{categoryId}")
    public ResponseEntity<ApiResponse> passQuiz(@PathVariable Integer categoryId,
                                                @CurrentUser User user,
                                                @RequestBody List<AnswerDTO> answers,
                                                @RequestParam Integer duration,
                                                @RequestParam Integer countAnswer) {
        return ResponseEntity.ok(quizService.passQuiz(categoryId, duration, user, answers, countAnswer));
    }


    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_USER')")
    @Operation(summary = "Lessonlarni orasidagi testni tugatish")
    @PostMapping("/pass/online/{lessonId}")
    public ResponseEntity<ApiResponse> passTestLesson(@PathVariable Integer lessonId,
                                                      @RequestParam Integer nextLessonId,
                                                      @CurrentUser User user,
                                                      @RequestBody List<AnswerDTO> answers) {
        ApiResponse apiResponse = quizService.passQuizForLesson(lessonId, nextLessonId, user, answers);
        return ResponseEntity.ok(apiResponse);
    }
}
