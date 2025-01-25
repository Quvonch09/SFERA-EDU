package com.example.sfera_education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.sfera_education.entity.Lesson;
import com.example.sfera_education.payload.UserLessonDTO;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    @Query(value = """
            select count(l) from Lesson l where l.module.category.id = :categoryId
            """)
    Integer countAllByCategoryId(Integer categoryId);

    List<Lesson> findByModuleIdAndDeletedFalse(Integer module_id);

    @Query(value = "select l.* from lesson as l join module as m on l.module_id = m.id " +
            "where category_id=:categoryId order by id limit 1", nativeQuery = true)
    Lesson findFirstLessonByCategoryId(@Param("categoryId") Integer categoryId);

    @Query(value = "select l.id as lessonId, l.name as name, " +
            "l.module_id as moduleId, up.completed as completed, l.quiz_category_settings_id as quizId from lesson as l " +
            "join user_progress as up on l.id=up.lesson_id join module as m on l.module_id = m.id " +
            "where m.category_id=:categoryId AND up.user_id=:userId and l.deleted is false", nativeQuery = true)
    List<UserLessonDTO> findUserLessons(@Param("categoryId") Integer categoryId, @Param("userId") Long userId);

    @Query(value = "select l.* from lesson as l join module as m on l.module_id=m.id " +
            "where m.category_id=:categoryId and l.deleted is false", nativeQuery = true)
    List<Lesson> findByCategory(@Param("categoryId") Integer categoryId);

    Lesson findByIdAndDeletedFalse(Integer lessonId);

    Integer countAllByModuleIdAndDeletedFalse(Integer moduleId);


    @Query(value = "select count(l.*) from lesson l join module m on l.module_id = m.id join category c on m.category_id = c.id where c.category_enum = ?1", nativeQuery = true)
    Integer countLesson(String category_enum);


    @Query(value = "select l from Lesson l where l.module.category.id = :categoryId and l.deleted = false")
    List<Lesson> findAllByCategoryId(Integer categoryId);


    @Query(value = "SELECT DISTINCT l.* " +
            "FROM lesson l " +
            "LEFT JOIN module m ON l.module_id = m.id " +
            "LEFT JOIN category c ON m.category_id = c.id " +
            "LEFT JOIN groups g ON g.category_id = c.id " +
            "LEFT JOIN users t ON g.teacher_id = t.id " +
            "WHERE (?1 IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR t.id = ?2) " +
            "AND (?3 IS NULL OR m.id = ?3) " +
            "AND (?4 IS NULL OR c.id = ?4) " +
            "AND (c.category_enum = ?5) " +
            "AND l.deleted = false " +
            "ORDER BY l.id DESC ",
            nativeQuery = true)
    Page<Lesson> searchLessons(@Param("name") String name,
                               @Param("teacherId") Long teacherId,
                               @Param("moduleId") Integer moduleId,
                               @Param("categoryId") Integer categoryId,
                               @Param("categoryEnum") String categoryEnum,
                               Pageable pageable);


    @Query(value = "select count(l.*) from lesson l " +
            "left join module m on l.module_id=m.id " +
            "left join category c on m.category_id=c.id " +
            "where c.category_enum='ONLINE'", nativeQuery = true)
    Integer countLesson();
}



