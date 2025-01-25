package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.IdList;
import com.example.sfera_education.payload.res.ResContactNotification;
import com.example.sfera_education.payload.res.ResNotification;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.NotificationService;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@CrossOrigin
public class NotificationController {

    private final NotificationService notificationService;


    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_USER','ROLE_STUDENT', 'ROLE_ADMIN')")
    @Operation(summary = "Barcha roldagilar oziga kelgan bildirishnomalarni koradi, admin ham")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllNotifications(@CurrentUser User user) {
        return ResponseEntity.ok(notificationService.getNotifications(user));
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_USER','ROLE_STUDENT')")
    @Operation(summary = "Barcha roldagilar oziga kelgan bildirishnomalarni sonini koradi")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> countNotifications(@CurrentUser User user) {
        return ResponseEntity.ok(notificationService.getCountNotification(user));
    }

    @PreAuthorize("hasAnyRole('ROLE_TEACHER','ROLE_USER','ROLE_STUDENT')")
    @Operation(summary = "Barcha roldagilar oziga kelgan bildirishnomalarni oqilgan qilishi")
    @PostMapping("/read")
    public ResponseEntity<ApiResponse> readNotifications(@RequestBody IdList idList) {
        return ResponseEntity.ok(notificationService.isReadAllNotification(idList));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_STUDENT')")
    @Operation(summary = "Online uchun barcha roldagilar oziga kelgan bildirishnomalarni oqishi")
    @GetMapping("/online/all")
    public ResponseEntity<ApiResponse> getOnlineNotifications(@CurrentUser User user) {
        return ResponseEntity.ok(notificationService.getOnlineNotification(user));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin hammaga notification jonatishi")
    @PostMapping("/send/all-users")
    public ResponseEntity<ApiResponse> sendAllUsersNotification(@RequestBody ResNotification resNotification,
                                                                @RequestParam(required = false, defaultValue = "0")
                                                                Long fileId) {
        return ResponseEntity.ok(notificationService.adminSendNotification(resNotification, fileId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "faqat admin uchun")
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> sendAllUsersNotification(@RequestParam Long id) {
        return ResponseEntity.ok(notificationService.deleteNotification(id));
    }


    @Operation(summary = "asosiy saytdagi kontact qismida ishlatiladi, data ichida contact=true chiqsa rangini boshqacha qilingla")
    @PostMapping("/contact")
    public ResponseEntity<ApiResponse> contactNotification(@RequestBody ResContactNotification contact) {
        return ResponseEntity.ok(notificationService.contactNotification(contact));
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/registrant")
    public ResponseEntity<ApiResponse> registrant(@CurrentUser User user) {
        return ResponseEntity.ok(notificationService.notification(user));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/del-registrant/{id}")
    public ResponseEntity<ApiResponse> delRegistrant(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.delRegistrant(id));
    }


}
