package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.auth.AuthRegister;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/update/role/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Faqat Admin uchun", description = "Admin user id bilan role kiritadi va ozgartiradi!")
    public ResponseEntity<ApiResponse> updateRole(@CurrentUser User user, @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateRole(user, id));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin student groupni uzgartirish")
    @PutMapping("/addStudentGroup/{userId}/{groupId}")
    public ResponseEntity<ApiResponse> addUserGroup(@PathVariable Long userId, @PathVariable Integer groupId) {
        ApiResponse apiResponse = userService.addUserGroup(userId, groupId);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin student groupni uzgartirish")
    @PutMapping("/updateStudentGroup/{userId}/{groupId}")
    public ResponseEntity<ApiResponse> updateUserGroup(@PathVariable Long userId, @PathVariable Integer groupId) {
        ApiResponse apiResponse = userService.updateUserGroup(userId, groupId);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER','ROLE_USER','ROLE_STUDENT')")
    @Operation(summary = "User uzini profilini update qilish")
    @PutMapping()
    public ResponseEntity<ApiResponse> updateUser(@CurrentUser User user,
                                                  @RequestBody AuthRegister auth,
                                                  @RequestParam(value = "fileId", defaultValue = "0", required = false) Long fileId) {
        ApiResponse apiResponse = userService.updateUser(user.getId(), auth, fileId);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER','ROLE_USER','ROLE_STUDENT')")
    @Operation(summary = "Barcha userlar uzini profilini kurish")
    @GetMapping("/get/me")
    public ResponseEntity<ApiResponse> getUser(@CurrentUser User user) {
        return ResponseEntity.ok(userService.getMe(user.getId()));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Admin userlarni category type buyicha get qilish",
            description = "faqat eduga foydalanuvchilar royxatiga student uchun teacher id bn search qiling")
    @GetMapping("/searchUser")
    public ResponseEntity<ApiResponse> searchStudent(@RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "teacherId", required = false) Long teacherId,
                                                     @RequestParam(value = "groupId", required = false) Integer groupId,
                                                     @RequestParam(value = "phone_number", required = false) String phoneNumber,
                                                     @RequestParam(value = "type") CategoryEnum type,
                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                     @CurrentUser User user) {
        ApiResponse allUsersByRole = userService.searchUser(user, name, teacherId, groupId, phoneNumber, type, page, size);
        return ResponseEntity.ok(allUsersByRole);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin ariza topshirgan roli user boganlani search qiladi")
    @GetMapping("/searchUserAdmin")
    public ResponseEntity<ApiResponse> searchUserAdmin(@RequestParam(value = "name", required = false) String name,
                                                       @RequestParam(value = "phone_number", required = false) String phoneNumber,
                                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                                       @CurrentUser User user) {
        ApiResponse allUsersByRole = userService.searchUserAdmin(name, phoneNumber, page, size);
        return ResponseEntity.ok(allUsersByRole);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin userlarni uchirish uchun")
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        ApiResponse apiResponse = userService.deleteUser(userId);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin User profilini update qilishi")
    @PutMapping("update/admin/{id}")
    public ResponseEntity<ApiResponse> updateUserByAdmin(@PathVariable Long id,
                                                         @RequestBody AuthRegister auth,
                                                         @RequestParam(value = "fileId", defaultValue = "0", required = false) Long fileId) {


        ApiResponse apiResponse = userService.updateUser(id, auth, fileId);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin bitta userni profilini kurish")
    @GetMapping("/get/one/{id}")
    public ResponseEntity<ApiResponse> getOneUser(@PathVariable Long id) {
        ApiResponse me = userService.getMe(id);
        return ResponseEntity.ok(me);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin teacherlarni kurishi")
    @GetMapping("/teachers")
    public ResponseEntity<ApiResponse> getTeachers() {
        return ResponseEntity.ok(userService.findAllTeacher());
    }


}