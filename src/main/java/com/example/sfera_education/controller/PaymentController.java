package com.example.sfera_education.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.sfera_education.entity.enums.PayType;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.res.ResPayment;
import com.example.sfera_education.service.PaymentService;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Paymentni saqlash uchun")
    @PostMapping
    public ResponseEntity<ApiResponse> savePayment(@RequestBody ResPayment resPayment,
                                                   @RequestParam PayType payType) {
        ApiResponse apiResponse = paymentService.savePayment(resPayment, payType);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Paymentni bittasini kurish uchun")
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> getPayment(@PathVariable Long paymentId) {
        ApiResponse onePayment = paymentService.getOnePayment(paymentId);
        return ResponseEntity.ok(onePayment);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Gruh ichidagi uquvchilarni tanlangan oyi buyicha tulov qilgan qilmaganligini bilish")
    @GetMapping("/students/{groupId}")
    public ResponseEntity<ApiResponse> getGroupId(@PathVariable Integer groupId,
                                                  @RequestParam int year,
                                                  @RequestParam int month) {
        ApiResponse onePayment = paymentService.getGroupPaymentStudent(groupId, year, month);
        return ResponseEntity.ok(onePayment);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Paymentlarni search qilish")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchPayment(@RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) PayType payType,
                                                     @RequestParam(required = false) String payDate,
                                                     @RequestParam(defaultValue = "0", required = false) int page,
                                                     @RequestParam(defaultValue = "10", required = false) int size) {
        if (payType == null) {
            ApiResponse allPayments1 = paymentService.getAllPayments(keyword, null, payDate, page, size);
            return ResponseEntity.ok(allPayments1);
        }
        ApiResponse allPayments = paymentService.getAllPayments(keyword, payType.name(), payDate, page, size);
        return ResponseEntity.ok(allPayments);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Paymentni uzgartirish uchun")
    @PutMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> updatePayment(@PathVariable Long paymentId,
                                                     @RequestBody ResPayment resPayment,
                                                     @RequestParam PayType payType) {
        ApiResponse apiResponse = paymentService.updatePayment(paymentId, resPayment, payType);
        return ResponseEntity.ok(apiResponse);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Paymentni uchirish uchun")
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> deletePayment(@PathVariable Long paymentId) {
        ApiResponse apiResponse = paymentService.deletePayment(paymentId);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Paymentlar bo'yicha yillik statistikasi")
    @GetMapping("/yearly")
    public ResponseEntity<ApiResponse> statisticPayment() {
        ApiResponse yearly = paymentService.getYearly();
        return ResponseEntity.ok(yearly);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @Operation(summary = "Paymentlarning total summasini kurish")
    @GetMapping("/totalSum")
    public ResponseEntity<ApiResponse> totalPaymentSum(@RequestParam(required = false) Integer month) {
        ApiResponse yearly = paymentService.getTotalPaySum(month);
        return ResponseEntity.ok(yearly);
    }


}
