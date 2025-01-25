package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.ResultService;

@Controller
@CrossOrigin
@RestController
@RequestMapping("/result")
@RequiredArgsConstructor
public class ResultController {
    private final ResultService resultService;


    @Operation(summary = "Student, User or Admin")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_STUDENT', 'ROLE_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getResultsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(resultService.getAllResults(userId));
    }


    @Operation(summary = "Student, User or Admin")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_STUDENT', 'ROLE_ADMIN')")
    @GetMapping("/{resultId}")
    public ResponseEntity<ApiResponse> getResult(@PathVariable Long resultId) {
        return ResponseEntity.ok(resultService.getResult(resultId));
    }


    @Operation(summary = "Quiz User uchun Resultning barchasi soni")
    @GetMapping("/countAll")
    public ResponseEntity<ApiResponse> countAll(@CurrentUser User user) {
        ApiResponse apiResponse = resultService.countAllResults(user);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Quiz User uchun Resultni foizda kurish")
    @GetMapping("/getPercentageByResult")
    public ResponseEntity<ApiResponse> getPercentageByResult(@CurrentUser User user) {
        ApiResponse resultByPercentage = resultService.getResultByPercentage(user);
        return ResponseEntity.ok(resultByPercentage);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(description = "status code -> 3=A'lo, 2=Yaxshi, 1=Yomon")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchResults(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryName", required = false) String categoryName,
            @RequestParam(value = "statusCode", required = false) Integer statusCode,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(resultService.searchResults(name, categoryName, statusCode, page, size));
    }

}
