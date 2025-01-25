package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sfera_education.entity.DayOfWeek;

public interface DayOfWeekRepository extends JpaRepository<DayOfWeek, Integer> {
}
