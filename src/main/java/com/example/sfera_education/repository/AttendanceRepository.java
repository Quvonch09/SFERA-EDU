package com.example.sfera_education.repository;

import com.example.sfera_education.entity.Attendance;
import com.example.sfera_education.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> getAttendanceByStudentIdAndDateBetween(Long id, LocalDate startDate, LocalDate endDate);

    Attendance findByStudentAndDate(User student, LocalDate date);
}
