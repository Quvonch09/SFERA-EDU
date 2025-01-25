package com.example.sfera_education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.sfera_education.entity.Result;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.res.WeekStatistic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {


    List<Result> findAllByUserId(@Param("userId") Long userId);


    Integer countAllByUserId(Long userId);

    @Query(value = """
            select count(*) from Result r where r.user=?1 and r.statusCode=2 or r.statusCode=3
            """)
    Integer countAllByResult(User user);


    Integer countAllByUserAndStatusCode(User user, int statusCode);

    @Query(value = "SELECT TO_CHAR(r.created_at, 'Day') as weekDay, COUNT(*) as count \n" +
            "FROM result r \n" +
            "WHERE r.created_at >= :startDate AND r.created_at < :endDate \n" +
            "GROUP BY TO_CHAR(r.created_at, 'Day'), EXTRACT(DOW FROM r.created_at) \n" +
            "ORDER BY EXTRACT(DOW FROM r.created_at)", nativeQuery = true
    )
    List<WeekStatistic> countResultsByDayOfWeek(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    Integer countAllByStatusCode(Integer statusCode);

    Integer countAllByCreatedAt(LocalDate createdAt);


    @Query(value = "SELECT r.* FROM result r " +
            "JOIN users u ON r.user_id = u.id " +
            "WHERE (:name IS NULL OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:categoryName IS NULL OR LOWER(r.category_name) LIKE LOWER(CONCAT('%', :categoryName, '%'))) " +
            "AND (:statusCode IS NULL OR r.status_code = :statusCode)", nativeQuery = true)
    Page<Result> searchResults(@Param("name") String name,
                               @Param("categoryName") String categoryName,
                               @Param("statusCode") Integer statusCode,
                               Pageable pageable);


}
