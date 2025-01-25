package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.auth.AuthLogin;
import com.example.sfera_education.payload.auth.AuthRegister;
import com.example.sfera_education.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> logIn(@Valid @RequestBody AuthLogin authLogin) {
        return ResponseEntity.ok(authService.login(authLogin));
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody AuthRegister authRegister) {
        return ResponseEntity.ok(authService.register(authRegister));
    }

    @Operation(summary = "Admin yangi userni ro'yxatdan o'tkazib guruhga qo'shadi")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/save-user")
    public ResponseEntity<ApiResponse> adminSaveUser(@Valid @RequestBody AuthRegister auth,
                                                     @RequestParam Integer groupId) {
        return ResponseEntity.ok(authService.adminSaveUser(auth, groupId));
    }

    @Operation(summary = "Admin yangi teacher qoshadi")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/admin/save-teacher")
    public ResponseEntity<ApiResponse> adminSaveTeacher(@Valid @RequestBody AuthRegister auth) {
        return ResponseEntity.ok(authService.adminSaveTeacher(auth));
    }

}
