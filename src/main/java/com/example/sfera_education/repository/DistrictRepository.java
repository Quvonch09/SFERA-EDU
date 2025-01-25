package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sfera_education.entity.District;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    boolean existsByName(String name);
}
