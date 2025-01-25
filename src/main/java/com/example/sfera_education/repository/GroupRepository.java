package com.example.sfera_education.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.example.sfera_education.entity.Group;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    Integer countByActiveTrue();

    boolean existsByName(String name);

    List<Group> findAllByTeacherIdAndActiveTrue(Long teacherId);

    @Query(value = "select * from groups as g join groups_students gs on g.id = gs.group_id where gs.students_id=:id", nativeQuery = true)
    Group findByUserId(@Param("id") Long id);

    List<Group> findAllByActiveTrue();

    List<Group> findAllByCategoryIdAndActiveTrue(Integer categoryId);

    int countAllByTeacherIdAndActiveTrue(Long teacherId);

    @Modifying
    @Transactional
    @Query(value = "delete from groups_students gs where gs.group_id =?1", nativeQuery = true)
    void deleteByGroupId(Integer id);
}


