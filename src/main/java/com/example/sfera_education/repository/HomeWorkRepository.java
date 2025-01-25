package com.example.sfera_education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.sfera_education.entity.HomeWork;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.StudentRatingDTO;
import com.example.sfera_education.payload.res.CategoryStatistics;
import com.example.sfera_education.payload.res.GroupStatistics;
import com.example.sfera_education.payload.res.StudentStatisticY;
import com.example.sfera_education.payload.res.WeekStatistic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface HomeWorkRepository extends JpaRepository<HomeWork, Integer> {

    HomeWork findByTaskIdAndStudentId(Integer taskId, Long userId);

    List<HomeWork> findByStudentId(Long id);

    List<HomeWork> findByStudentIdAndCheckedFalse(Long id);

    boolean existsByTaskIdAndStudentId(Integer taskId, Long userId);

    @Query("SELECT SUM(hm.score) FROM HomeWork hm WHERE hm.student.id = :studentId AND hm.checked = true")
    Integer findTotalScoreByStudent(@Param("studentId") Long studentId);


    @Query(value = "SELECT u.firstname AS firstname, u.lastname AS lastname, u.phone_number AS phoneNumber, " +
            "COALESCE(SUM(h.score), 0) AS score " +
            "FROM home_work h " +
            "INNER JOIN users u ON h.student_id = u.id " +
            "WHERE u.group_id = :groupId " +
            "GROUP BY u.firstname, u.lastname, u.phone_number " +
            "ORDER BY score DESC", nativeQuery = true)
    List<StudentRatingDTO> getRatingStudents(@Param("groupId") Integer groupId);


    @Query(value = """ 
            SELECT SUM(hw.score) FROM home_work hw
            JOIN Users u ON hw.student_id = u.id
            WHERE u.group_id = :group_id
            AND hw.due_date >= :startDate
            AND hw.due_date <= :endDate
            """, nativeQuery = true)
    Integer findTotalScoreByGroupAndPeriod(@Param("group_id") Integer group,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);


    @Query("SELECT SUM(hw.score) FROM HomeWork hw WHERE hw.student IN :student AND hw.dueDate >= :startDate AND hw.dueDate <= :endDate AND hw.score IS NOT NULL")
    Integer findTotalScoreByStudentsAndPeriod(@Param("student") User student, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query(value = "SELECT c.name as categoryName, TO_CHAR(hw.due_date,'Month') as month, COALESCE(SUM(hw.score),0) as totalScore " +
            "FROM home_work hw " +
            "JOIN users u ON hw.student_id = u.id " +
            "JOIN groups g ON u.group_id = g.id " +
            "JOIN category c ON g.category_id = c.id " +
            "WHERE hw.score IS NOT NULL " +
            "GROUP BY c.name, TO_CHAR(hw.due_date, 'Month'),EXTRACT(MONTH FROM hw.due_date) " +
            "ORDER BY c.name, EXTRACT(MONTH FROM hw.due_date)", nativeQuery = true)
    List<CategoryStatistics> findCategoryStatistics();

    @Query(value = "SELECT g.name as groupName, TO_CHAR(hw.due_date, 'Month') as month, " +
            "COALESCE(SUM(hw.score), 0) as totalScore \n" +
            "FROM home_work hw \n" +
            "JOIN users u ON hw.student_id = u.id \n" +
            "JOIN groups g ON u.group_id = g.id \n" +
            "WHERE hw.score IS NOT NULL \n" +
            "GROUP BY g.name, TO_CHAR(hw.due_date, 'Month'), EXTRACT(MONTH FROM hw.due_date) \n" +
            "ORDER BY g.name, EXTRACT(MONTH FROM hw.due_date)", nativeQuery = true
    )
    List<GroupStatistics> findGroupStatistics();


    @Query(value = "SELECT g.name as groupName, TO_CHAR(hw.due_date,'Month') as month, COALESCE(SUM(hw.score),0) " +
            " as totalScore " +
            "FROM home_work hw " +
            "JOIN users u ON hw.student_id = u.id " +
            "JOIN groups g ON u.group_id = g.id " +
            "WHERE g.teacher_id=:teacherId AND hw.score IS NOT NULL " +
            "GROUP BY g.name, TO_CHAR(hw.due_date, 'Month'), EXTRACT(MONTH FROM hw.due_date)" +
            "ORDER BY g.name, EXTRACT(MONTH FROM hw.due_date)", nativeQuery = true)
    List<GroupStatistics> findGroupStatisticsByTeacher(@Param("teacherId") Long teacherId);

    @Query(value = "SELECT TO_CHAR(hw.due_date, 'Month') as monthName, \n" +
            "       EXTRACT(MONTH FROM hw.due_date) as monthNumber, \n" +
            "       COALESCE(SUM(hw.score),0) as totalScore \n" +
            "FROM home_work hw \n" +
            "JOIN users u ON hw.student_id = u.id \n" +
            "WHERE u.id = ?1 \n" +
            "  AND u.role = 'ROLE_STUDENT' \n" +
            "  AND hw.score IS NOT NULL \n" +
            "GROUP BY TO_CHAR(hw.due_date, 'Month'), EXTRACT(MONTH FROM hw.due_date) \n" +
            "ORDER BY monthNumber", nativeQuery = true

    )
    List<StudentStatisticY> findStudentStatisticYearly(Long studentId);

    @Query(value = "SELECT TO_CHAR(hw.due_date, 'Day') AS weekDay, COUNT(*) AS count " +
            "FROM home_work hw " +
            "WHERE hw.due_date >= :startDate AND hw.due_date < :endDate " +
            "AND hw.student_id = :studentId " +
            "GROUP BY TO_CHAR(hw.due_date, 'Day')", nativeQuery = true)
    List<WeekStatistic> countHomeWorkByDayOfWeek(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 @Param("studentId") Long studentId);


    @Query(value = "select hw.* from home_work hw join users u on hw.student_id=u.id " +
            "join groups g on u.group_id=g.id where g.teacher_id = ?1 and hw.checked=true and (?2 IS NULL OR LOWER(u.firstname) " +
            "LIKE LOWER(CONCAT('%', ?2, '%'))\n" +
            "OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', ?2, '%'))) and (?3 IS NULL OR g.id= ?3)", nativeQuery = true)
    Page<HomeWork> getAllByFilter(Long teacherId, String keyword, Integer groupId, PageRequest request);


}
