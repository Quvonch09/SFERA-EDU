package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.QuestionDto;
import com.example.sfera_education.service.QuestionService;


@CrossOrigin
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "ADMIN Questionni save qilish uchun")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public HttpEntity<ApiResponse> saveQuestion(@RequestParam(required = false, defaultValue = "0") Integer categoryId,
                                                @RequestParam(required = false, defaultValue = "0") Integer lessonId,
                                                @RequestBody QuestionDto questionDto) {
        ApiResponse apiResponse = questionService.saveQuestions(categoryId, lessonId, questionDto);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Questionni bittasini get qilish uchun")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_USER')")
    @GetMapping("/{id}")
    public HttpEntity<ApiResponse> getOneQuestion(@PathVariable Integer id) {
        ApiResponse apiResponse = questionService.getOneQuestion(id);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "ADMIN Questionni update qilish uchun")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{questionId}")
    public HttpEntity<ApiResponse> updateQuestion(@RequestBody QuestionDto questionDto,
                                                  @PathVariable Integer questionId) {
        ApiResponse apiResponse = questionService.updateQuestion(questionId, questionDto);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "ADMIN Questionni delete qilish uchun")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<ApiResponse> deleteQuestion(@PathVariable Integer id) {
        ApiResponse apiResponse = questionService.deleteQuestion(id);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "ADMIN  Questionni search qilish uchun")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> searchQuestion(@RequestParam(required = false) String questionName,
                                                      @RequestParam(required = false) Integer categoryId,
                                                      @RequestParam(required = false) Integer lessonId,
                                                      @Param("categoryEnum") CategoryEnum categoryEnum,
                                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        ApiResponse apiResponse = questionService.searchQuestionByQuizCategory(questionName, categoryId, lessonId, categoryEnum, page, size);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_USER')")
    @Operation(summary = "lessondagi questionlarni barchasini kurish")
    @GetMapping("/byLesson/{lessonId}")
    public ResponseEntity<ApiResponse> getQuestionByLesson(@PathVariable Integer lessonId) {
        ApiResponse questionListByLesson = questionService.getQuestionListByLesson(lessonId);
        return ResponseEntity.ok(questionListByLesson);
    }
}
