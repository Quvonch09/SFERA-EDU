package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.DistrictDTO;
import com.example.sfera_education.service.DistrictService;

@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping("/district")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class DistrictController {

    private final DistrictService districtService;

    @Operation(summary = "Admin district save qilish")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> saveDistrict(@RequestBody DistrictDTO resDistrict) {
        ApiResponse apiResponse = districtService.saveDistrict(resDistrict);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Admin district hammasini get qilish")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getDistricts() {
        ApiResponse allDistricts = districtService.getAllDistricts();
        return ResponseEntity.ok(allDistricts);
    }


    @Operation(summary = "Admin districtni bittasini get qilish")
    @GetMapping("/{districtId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getDistrictById(@PathVariable Integer districtId) {
        ApiResponse districtById = districtService.getDistrictById(districtId);
        return ResponseEntity.ok(districtById);
    }


    @Operation(summary = "Admin districtni update qilish")
    @PutMapping("/{districtId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> updateDistrict(@PathVariable Integer districtId, @RequestBody DistrictDTO resDistrict) {
        ApiResponse apiResponse = districtService.updateDistrict(districtId, resDistrict);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Admin districtni bittasini delete qilish")
    @DeleteMapping("/{districtId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteDistrict(@PathVariable Integer districtId) {
        ApiResponse apiResponse = districtService.deleteDistrict(districtId);
        return ResponseEntity.ok(apiResponse);
    }
}
