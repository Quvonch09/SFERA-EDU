package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sfera_education.entity.Category;

public interface QuizCategoryRepository extends JpaRepository<Category, Integer> {


}

