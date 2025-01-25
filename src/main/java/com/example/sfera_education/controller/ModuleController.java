package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.res.ResModule;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.ModuleService;

@CrossOrigin
@RestController
@RequestMapping("/module")
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    @Operation(summary = "ADMIN/TEACHER Modul save qilish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveModule(@RequestBody ResModule moduleDTO) {
        ApiResponse apiResponse = moduleService.saveModule(moduleDTO);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_USER')")
    @Operation(summary = "Modul bittasini get qilish")
    @GetMapping("/{moduleId}")
    public ResponseEntity<ApiResponse> getModuleById(@PathVariable Integer moduleId) {
        ApiResponse module = moduleService.getOneModule(moduleId);
        return ResponseEntity.ok(module);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    @Operation(summary = "ADMIN/TEACHER Modul update qilish")
    @PutMapping("/{moduleId}")
    public ResponseEntity<ApiResponse> updateModule(@PathVariable Integer moduleId,
                                                    @RequestBody ResModule moduleDTO) {
        ApiResponse apiResponse = moduleService.updateModule(moduleId, moduleDTO);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "ADMIN/TEACHER Modul delete qilish")
    @DeleteMapping("/{moduleId}")
    public ResponseEntity<ApiResponse> deleteModule(@PathVariable Integer moduleId) {
        ApiResponse apiResponse = moduleService.deleteModule(moduleId);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_USER')")
    @Operation(summary = "Categoryaga tegishli barcha modullarni get qilish")
    @GetMapping("/byCategory/{categoryId}")
    public ResponseEntity<ApiResponse> getModuleByCategory(@PathVariable Integer categoryId, @CurrentUser User user) {
        ApiResponse module = moduleService.getModuleByCategoryId(categoryId, user);
        return ResponseEntity.ok(module);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_USER')")
    @Operation(summary = "Online Categoryaga tegishli barcha modullarni search qilish")
    @GetMapping("/searchModuleOnline")
    public ResponseEntity<ApiResponse> searchModuleOnline(@RequestParam(required = false) String moduleName,
                                                          @RequestParam(required = false) Integer categoryId) {
        ApiResponse apiResponse = moduleService.searchModule(moduleName, categoryId, CategoryEnum.ONLINE.name());
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_USER')")
    @Operation(summary = "Education Categoryaga tegishli barcha modullarni search qilish")
    @GetMapping("/searchModuleEducation")
    public ResponseEntity<ApiResponse> searchModuleEducation(@RequestParam(required = false) String moduleName,
                                                             @RequestParam(required = false) Integer categoryId) {
        ApiResponse apiResponse = moduleService.searchModule(moduleName, categoryId, CategoryEnum.EDUCATION.name());
        return ResponseEntity.ok(apiResponse);
    }

}