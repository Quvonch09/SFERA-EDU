package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.res.ResContact;
import com.example.sfera_education.service.ContactService;

@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping("/contact")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ContactController {

    private final ContactService contactService;

    @Operation(summary = "Admin contact save qilish")
    @PostMapping
    public ResponseEntity<ApiResponse> saveContact(@RequestBody ResContact resContact) {
        ApiResponse apiResponse = contactService.saveContact(resContact);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Admin contact hammasini get qilish")
    @GetMapping
    public ResponseEntity<ApiResponse> getContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @Operation(summary = "Admin contactni bittasini get qilish")
    @GetMapping("/{contactId}")
    public ResponseEntity<ApiResponse> getContactById(@PathVariable Integer contactId) {
        return ResponseEntity.ok(contactService.getContactById(contactId));
    }


    @Operation(summary = "Admin contact update qilish")
    @PutMapping("/{contactId}")
    public ResponseEntity<ApiResponse> updateContact(@PathVariable Integer contactId, @RequestBody ResContact resContact) {
        ApiResponse apiResponse = contactService.updateContact(contactId, resContact);
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Admin contact bittasini delete qilish")
    @DeleteMapping("/{contactId}")
    public ResponseEntity<ApiResponse> deleteContact(@PathVariable Integer contactId) {
        ApiResponse apiResponse = contactService.deleteContact(contactId);
        return ResponseEntity.ok(apiResponse);
    }
}
