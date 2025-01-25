package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sfera_education.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Integer> {
    boolean existsByName(String name);
}
