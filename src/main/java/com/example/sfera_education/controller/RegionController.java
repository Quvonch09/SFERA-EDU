package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.RegionDTO;
import com.example.sfera_education.service.RegionService;

@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping("/region")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class RegionController {
    private final RegionService regionService;


    @Operation(summary = "Admin region qushish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveRegion(@RequestBody RegionDTO resRegion) {
        ApiResponse apiResponse = regionService.saveRegion(resRegion);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Admin regionlarni listini kurish")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllRegions() {
        ApiResponse allRegion = regionService.getAllRegion();
        return ResponseEntity.ok(allRegion);
    }


    @Operation(summary = "Admin regionni bittasini kurish")
    @GetMapping("/{regionId}")
    public ResponseEntity<ApiResponse> getRegionById(@PathVariable Integer regionId) {
        ApiResponse oneRegion = regionService.getOneRegion(regionId);
        return ResponseEntity.ok(oneRegion);
    }

    @Operation(summary = "Admin regionni update qilish")
    @PutMapping("/{regionId}")
    public ResponseEntity<ApiResponse> updateRegion(@PathVariable Integer regionId, @RequestBody RegionDTO resRegion) {
        ApiResponse apiResponse = regionService.updateRegion(regionId, resRegion);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Admin regionni delete qilish")
    @DeleteMapping("/{regionId}")
    public ResponseEntity<ApiResponse> deleteRegion(@PathVariable Integer regionId) {
        ApiResponse apiResponse = regionService.deleteRegion(regionId);
        return ResponseEntity.ok(apiResponse);
    }
}
