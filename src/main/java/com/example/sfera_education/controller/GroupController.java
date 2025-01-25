package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.res.ResGroup;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.GroupService;

@CrossOrigin
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin group qushish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveGroup(@RequestBody ResGroup resGroup) {
        ApiResponse apiResponse = groupService.saveGroup(resGroup);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin grouplarni hamasini kurish")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getGroup() {
        ApiResponse allGroups = groupService.getAllGroups();
        return ResponseEntity.ok(allGroups);
    }


    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "Teacher guruhlarni ko'rish")
    @GetMapping("/teacher")
    public ResponseEntity<ApiResponse> getTeacherGroup(@CurrentUser User user) {
        return ResponseEntity.ok(groupService.getTeacherGroups(user));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT')")
    @Operation(summary = "groupni bittasini kurish")
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse> getGroupById(@PathVariable Integer groupId) {
        ApiResponse group = groupService.getGroupById(groupId);
        return ResponseEntity.ok(group);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admion groupni update qilish")
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse> updateGroup(@PathVariable Integer groupId, @RequestBody ResGroup resGroup) {
        ApiResponse group = groupService.updateGroup(groupId, resGroup);
        return ResponseEntity.ok(group);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin groupni delete qilish")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse> deleteGroup(@PathVariable Integer groupId) {
        ApiResponse group = groupService.deleteGroup(groupId);
        return ResponseEntity.ok(group);
    }
}

