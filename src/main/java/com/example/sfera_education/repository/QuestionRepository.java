package com.example.sfera_education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.sfera_education.entity.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query(value = "SELECT q.* FROM question q " +
            "LEFT JOIN category c ON q.category_id = c.id " +
            "LEFT JOIN lesson l ON q.lesson_id = l.id " +
            "LEFT JOIN module m ON l.module_id = m.id " +
            "WHERE (?1 IS NULL OR LOWER(q.name) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR c.id = ?2) " +
            "AND (?3 IS NULL OR l.id = ?3) " +
            "OR (m.category_id = c.id) " +
            "AND c.category_enum = ?4 ",
            nativeQuery = true)
    Page<Question> searchNameAndCategoryId(String text, Integer categoryId,
                                           Integer lessonId,
                                           String categoryEnum,
                                           PageRequest pageRequest);


    List<Question> findAllByLessonId(Integer lessonId);

    int countAllByLessonId(Integer lessonId);

    @Query(value = """
            SELECT q.* FROM question q
            WHERE q.category_id = ?1
            ORDER BY RANDOM()
            LIMIT ?2
            """, nativeQuery = true)
    List<Question> findByCategoryIdAndRandom(Integer categoryId, Integer quizCount);


}
