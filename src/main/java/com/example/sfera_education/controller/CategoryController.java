package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.CategoryDTO;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.CategoryService;


@Controller
@CrossOrigin
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @Operation(summary = "ADMIN uchun")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/save/category")
    public ResponseEntity<ApiResponse> saveCategory(@RequestBody CategoryDTO categoryDTO, @RequestParam("categoryEnum") CategoryEnum categoryEnum) {
        ApiResponse apiResponse = categoryService.saveCategory(categoryDTO, categoryEnum);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_USER')")
    @Operation(summary = "Bitta categoriyani ko'rish")
    @GetMapping("/get-one/{id}")
    public ResponseEntity<ApiResponse> getOneCategory(@PathVariable Integer id) {
        ApiResponse apiResponse = categoryService.getOneCategory(id);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Category hammasini  kurish enum type boyicha")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getAllCategoryList(@Param("categoryEnum") CategoryEnum categoryEnum, @CurrentUser User user) {
        ApiResponse allCategory = categoryService.getAllCategories(categoryEnum, user);
        return ResponseEntity.ok(allCategory);
    }


    @Operation(summary = "Teacher ozi dars beradigan categoryni get qilishi")
    @GetMapping("/teacher")
    public ResponseEntity<ApiResponse> getAllCategoryByTeacher(@CurrentUser User user) {
        return ResponseEntity.ok(categoryService.getCategoryByTeacher(user));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN Categoryni update qilish")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable("id") Integer id, @RequestBody CategoryDTO categoryDTO) {
        ApiResponse apiResponse = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN  Categoryni delete qilish")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Integer id) {
        ApiResponse apiResponse = categoryService.deleteCategory(id);
        return ResponseEntity.ok(apiResponse);
    }


}
