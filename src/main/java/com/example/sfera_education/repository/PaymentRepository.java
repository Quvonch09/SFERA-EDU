package com.example.sfera_education.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.sfera_education.entity.Payment;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.PaymentYearly;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = "SELECT * FROM payment p WHERE " +
            "(COALESCE(:userId, p.student_id) = p.student_id) " +
            "AND (COALESCE(:payDate, p.pay_date) = p.pay_date) " +
            "AND (COALESCE(:payType, p.pay_type) = p.pay_type)",
            nativeQuery = true)
    Page<Payment> searchAllPayments(@Param("userId") Long userId,
                                    @Param("payDate") LocalDate payDate,
                                    @Param("payType") String payType,
                                    Pageable pageable);


    @Query(value = "SELECT  TO_CHAR(p.created_at ,'Month') as month, COALESCE(SUM(p.pay_sum),0) as totalPay from payment p " +
            "GROUP BY  TO_CHAR(p.created_at, 'Month'),EXTRACT(MONTH FROM p.created_at) " +
            "ORDER BY  EXTRACT(MONTH FROM p.created_at )", nativeQuery = true)
    List<PaymentYearly> findPaymentStatistics();

    @Query("SELECT SUM(p.paySum) FROM Payment p WHERE p.payDate BETWEEN :startDate AND :endDate")
    Double findTotalPaymentBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(p.paySum), 0) FROM Payment p WHERE p.student = :student AND YEAR(p.payDate) = :year AND MONTH(p.payDate) = :month")
    Double findTotalPaymentByStudentAndPayDate(@Param("student") User student, @Param("year") int year, @Param("month") int month);

}
