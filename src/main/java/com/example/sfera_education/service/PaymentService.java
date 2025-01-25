package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Group;
import com.example.sfera_education.entity.Payment;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.PayType;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.PaymentDTO;
import com.example.sfera_education.payload.PaymentYearly;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.ResPageable;
import com.example.sfera_education.payload.res.ResPayment;
import com.example.sfera_education.payload.res.ResPaymentSum;
import com.example.sfera_education.payload.res.ResPaymentUser;
import com.example.sfera_education.repository.GroupRepository;
import com.example.sfera_education.repository.PaymentRepository;
import com.example.sfera_education.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ApiResponse savePayment(ResPayment resPayment, PayType payType) {
        User user = userRepository.findById(resPayment.getUserId()).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        Payment payment = Payment.builder()
                .payDate(resPayment.getPayDate())
                .payType(payType)
                .student(user)
                .paySum(resPayment.getPaySum())
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);
        return new ApiResponse("Successfully saved payment");
    }


    public ApiResponse getOnePayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Payment"));
        }
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .paymentId(payment.getId())
                .paySum(payment.getPaySum())
                .paymentType(payment.getPayType().name())
                .userName(payment.getStudent().getLastname() + " " + payment.getStudent().getFirstname())
                .createdAt(payment.getCreatedAt())
                .payDate(payment.getPayDate())
                .build();
        return new ApiResponse(paymentDTO);
    }


    public ApiResponse getAllPayments(String studentName, String payType, String payDate, int page, int size) {
        LocalDate localDate = null;

        if (payDate != null) {
            localDate = LocalDate.parse(payDate);
        }

        if (studentName == null) {
            return new ApiResponse(resPageable(null, localDate, payType, page, size));
        }

        List<User> byUserByName = userRepository.findUserByKeyword(studentName);

        if (byUserByName.isEmpty()) {
            List<PaymentDTO> paymentDTOList = new ArrayList<>();
            ResPageable resPageable = ResPageable.builder()
                    .page(page)
                    .size(size)
                    .totalPage(0)
                    .totalElements(0)
                    .body(paymentDTOList)
                    .build();
            return new ApiResponse(resPageable);
        }

        List<ResPageable> resPageables = new ArrayList<>();

        for (User user : byUserByName) {
            ResPageable resPageable = resPageable(user.getId(), localDate, payType, page, size);
            if (resPageable.getTotalElements() != 0) {
                return new ApiResponse(resPageable);
            }
        }
        return new ApiResponse(ResponseError.NOTFOUND("Payment"));
    }


    public ApiResponse updatePayment(Long paymentId, ResPayment resPayment, PayType payType) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Payment"));
        }

        User user = userRepository.findById(resPayment.getUserId()).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        payment.setStudent(user);
        payment.setPaySum(resPayment.getPaySum());
        payment.setPayType(payType);
        payment.setPayDate(resPayment.getPayDate());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        return new ApiResponse("Successfully updated payment");
    }


    public ApiResponse deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Payment"));
        }
        paymentRepository.delete(payment);
        return new ApiResponse("Successfully deleted payment");
    }

    public ApiResponse getYearly() {
        List<PaymentYearly> paymentStatistics = paymentRepository.findPaymentStatistics();
        if (paymentStatistics.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("No payment statistics"));
        }
        return new ApiResponse(paymentStatistics);
    }


    public ApiResponse getTotalPaySum(Integer month) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth;
        LocalDate lastDayOfMonth;
        if (month != null) {
            firstDayOfMonth = LocalDate.of(today.getYear(), Month.of(month), 1);
            lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        } else {
            YearMonth currentMonth = YearMonth.from(today);
            firstDayOfMonth = currentMonth.atDay(1);
            lastDayOfMonth = currentMonth.atEndOfMonth();
        }
        Double totalPaymentBetweenDates = paymentRepository.findTotalPaymentBetweenDates(firstDayOfMonth, lastDayOfMonth);
        ResPaymentSum resPaymentSum = ResPaymentSum.builder()
                .startDate(firstDayOfMonth)
                .endDate(lastDayOfMonth)
                .totalSum(totalPaymentBetweenDates)
                .build();
        return new ApiResponse(resPaymentSum);
    }


    private ResPageable resPageable(Long id, LocalDate payDate, String payType, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Payment> payments = paymentRepository.searchAllPayments(id, payDate, payType, pageRequest);
        List<PaymentDTO> paymentDTOS = new ArrayList<>();
        for (Payment payment : payments) {
            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .paymentId(payment.getId())
                    .paySum(payment.getPaySum())
                    .paymentType(payment.getPayType().name())
                    .userName(payment.getStudent().getLastname() + " " + payment.getStudent().getFirstname())
                    .userId(payment.getStudent().getId())
                    .userGroupId(payment.getStudent().getGroupId())
                    .createdAt(payment.getCreatedAt())
                    .payDate(payment.getPayDate())
                    .build();
            paymentDTOS.add(paymentDTO);
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(payments.getSize())
                .totalElements(payments.getTotalElements())
                .body(paymentDTOS)
                .build();
        return resPageable;
    }

    public ApiResponse getGroupPaymentStudent(Integer groupId, int year, int month) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        List<ResPaymentUser> result = new ArrayList<>();
        for (User student : group.getStudents()) {
            Double sumStudentPayment =
                    paymentRepository.findTotalPaymentByStudentAndPayDate(student, year, month);
            result.add(
                    ResPaymentUser.builder()
                            .firstName(student.getFirstname())
                            .lastName(student.getLastname())
                            .payment(sumStudentPayment)
                            .build()
            );
        }
        return new ApiResponse(result);
    }
}
