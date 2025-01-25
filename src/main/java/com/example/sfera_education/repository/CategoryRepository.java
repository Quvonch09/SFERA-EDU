package com.example.sfera_education.repository;

import com.example.sfera_education.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.sfera_education.entity.Category;
import com.example.sfera_education.entity.enums.CategoryEnum;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByNameIgnoreCaseAndCategoryEnum(String name, CategoryEnum categoryEnum);

    List<Category> findAllByCategoryEnumAndActiveTrue(CategoryEnum categoryEnum);

    Integer countByActiveTrue();

    @Query(value = "select c.* from category as c join users_categories as uc on c.id=uc.categories_id " +
            "where uc.user_id=:userId", nativeQuery = true)
    List<Category> findAllByUserId(@Param("userId") Long userId);


    @Query(value = "select count(c.id) from category c join result r on c.name = r.category_name where r.user_id=:userId and c.category_enum=:categoryEnum", nativeQuery = true)
    Integer countAllByResultAndUserId(@Param("userId") Long userId, @Param("categoryEnum") String categoryEnum);

    Integer countByCategoryEnum(CategoryEnum categoryEnum);

    @Query(value = """
            select c from Category c
            join Group g on g.category.id = c.id
            where g.teacher.id = :teacherId
            """)
    List<Category> findAllByTeacherId(Long teacherId);

}
