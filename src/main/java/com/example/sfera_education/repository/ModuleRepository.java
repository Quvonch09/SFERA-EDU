package com.example.sfera_education.repository;

import com.example.sfera_education.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Integer> {

    List<Module> findAllByCategoryIdAndDeletedFalse(Integer id);

    @Query(value = "select id from module where category_id=:categoryId", nativeQuery = true)
    List<Integer> findAllIds(@Param("categoryId") Integer categoryId);

    boolean existsByNameIgnoreCaseAndCategory_Id(String name, Integer categoryId);

    Integer countAllByCategory_IdAndDeletedFalse(Integer categoryId);

    @Query(value = "select count(m.*) from module m join category c on m.category_id=c.id where c.category_enum = ?1 and m.deleted=false", nativeQuery = true)
    Integer countAllByModule(String categoryEnum);


    @Query(value = "SELECT m.* FROM module m " +
            "INNER JOIN category c ON c.id = m.category_id " +
            "WHERE (?1 IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR c.id = ?2) " +
            "AND c.category_enum = ?3 " +
            "AND m.deleted = false", nativeQuery = true)
    List<Module> searchAllByModule(String name, Integer categoryId, String categoryEnum);

}
