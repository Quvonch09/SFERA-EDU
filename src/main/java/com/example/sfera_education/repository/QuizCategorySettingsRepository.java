package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.sfera_education.entity.QuizCategorySettings;

@Repository
public interface QuizCategorySettingsRepository extends JpaRepository<QuizCategorySettings, Integer> {

    QuizCategorySettings findByCategoryId(Integer categoryId);

}
