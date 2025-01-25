package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.sfera_education.entity.LessonTracking;

import java.time.LocalDate;
import java.util.List;

public interface LessonTrackingRepository extends JpaRepository<LessonTracking, Integer> {

    boolean existsByGroupIdAndLessonIdAndActiveTrue(Integer groupId, Integer lesson);

    Integer countAllByGroupIdAndActiveTrue(Integer groupId);

    LessonTracking findByGroupIdAndLessonId(Integer groupId, Integer lessonId);

    List<LessonTracking> findByGroupIdAndActiveTrueAndCreatDateBetween(Integer groupId, LocalDate startDate, LocalDate endDate);


    @Query(value = "select * from lesson_tracking as lt  where lt.creat_date<= ?1 and lt.group_id = ?2 order by  lt.creat_date desc limit 1", nativeQuery = true)
    LessonTracking findCurrentLessonTracking(LocalDate now, Integer groupId);

}
