package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sfera_education.entity.UserProgress;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    boolean existsByUserIdAndLessonId(Long userId, Integer lessonId);

    UserProgress findByUserIdAndLessonId(Long userId, Integer lessonId);

}
