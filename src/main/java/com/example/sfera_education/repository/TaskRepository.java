package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sfera_education.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByLessonId(Integer lessonId);

    Integer countAllByLessonId(Integer lessonId);


}