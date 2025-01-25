package com.example.sfera_education.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.ERole;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    int countByRoleAndEnabledTrue(ERole role);

    User findByIdAndRoleAndEnabledTrue(Long id, ERole role);

    List<User> findByRole(ERole role);

    @Query(value = """
             SELECT u.*
            FROM users u
            LEFT JOIN groups g ON u.group_id = g.id
            WHERE (:name IS NULL OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%'))
                   OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:teacherId IS NULL OR :teacherId = g.teacher_id)
            AND (:groupId IS NULL OR :groupId = u.group_id)
            AND (:phoneNumber IS NULL OR u.phone_number LIKE CONCAT('%', :phoneNumber, '%'))
            AND (u.role = 'ROLE_STUDENT' )
            """, nativeQuery = true)
    Page<User> searchNameOrTeacherIdOrGroupIdOrPhoneNumber(@Param("name") String name,
                                                           @Param("teacherId") Long teacherId,
                                                           @Param("groupId") Integer groupId,
                                                           @Param("phoneNumber") String phoneNumber,
                                                           PageRequest pageRequest);

    @Query(value = """
            SELECT u.*
            FROM users u
            INNER JOIN notification n on n.registrant_id = u.id
            WHERE (:name IS NULL OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%'))
                   OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:phoneNumber IS NULL OR u.phone_number LIKE CONCAT('%', :phoneNumber, '%'))
            AND (u.role = 'ROLE_USER')
            """, nativeQuery = true)
    Page<User> searchNameOrPhoneNumber(@Param("name") String name,
                                       @Param("phoneNumber") String phoneNumber,
                                       PageRequest pageRequest);


    @Query(value = """
            SELECT DISTINCT u.*
            FROM users u
            INNER JOIN home_work hw ON hw.student_id = u.id
            LEFT JOIN groups g ON u.group_id = g.id
            WHERE g.teacher_id = ?1
            AND hw.checked = false
            AND u.role = 'ROLE_STUDENT'
            """, nativeQuery = true)
    List<User> findAllByTeacherIdAndHomeworkIsCheckedFalse(@Param("teacherId") Long teacherId);


    @Query(value = "SELECT u.* FROM users u " +
            "INNER JOIN groups g ON g.id = u.group_id " +
            "INNER JOIN category c ON c.id = g.category_id " +
            "WHERE (?1 IS NULL OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR ?1 IS NULL OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND (?2 IS NULL OR g.id = ?2) " +
            "AND (?3 IS NULL OR c.id = ?3) " +
            "AND u.role = 'ROLE_STUDENT' ", nativeQuery = true)
    Page<User> findAlLByStudentSearchAndGroupIdAndCategoryId(String name, Integer groupId, Integer categoryId, PageRequest request);

    @Query(value = "SELECT u.* FROM users u " +
            "INNER JOIN groups g ON g.id = u.group_id " +
            "WHERE (?1 IS NULL OR g.id = ?1) " +
            "AND ( g.teacher_id = ?2) AND u.role= 'ROLE_STUDENT' ", nativeQuery = true)
    Page<User> findAllByGroupId(Integer groupId, Long teacherId, PageRequest request);

    @Modifying
    @Transactional
    @Query(value = "insert into users_categories(user_id, categories_id) values(:userId, :categoryId)", nativeQuery = true)
    void addCategoryToUser(@Param("userId") Long userId, @Param("categoryId") Integer categoryId);


    @Query(value = "SELECT position FROM (" +
            "    SELECT u.id, " +
            "           ROW_NUMBER() OVER (ORDER BY SUM(h.score) DESC) AS position " +
            "    FROM home_work h " +
            "    INNER JOIN users u ON h.student_id = u.id " +
            "    WHERE u.group_id = :groupId " +
            "    GROUP BY u.id " +
            ") AS ranked_users " +
            "WHERE id = :userId", nativeQuery = true)
    Integer getRatingStudent(@Param("groupId") Integer groupId, @Param("userId") Long userId);


    @Query(value = """
            select count(u) from User u
            inner join Group g ON u.groupId = g.id
            where u.role = 'ROLE_STUDENT' and g.teacher.id = :teacherId
            """)
    int countAllByStudentByTeacher(Long teacherId);


    @Query(value = """
            select u from User u
            inner join Group g ON u.groupId = g.id
            where u.role = 'ROLE_STUDENT' and g.teacher.id = :teacherId
            """)
    List<User> findAllByStudentByTeacher(Long teacherId);


    @Query(value = """
            SELECT u.* FROM users u
            INNER JOIN result r ON r.user_id = u.id
            WHERE (:name IS NULL OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%'))
                 OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:phoneNumber IS NULL OR u.phone_number LIKE CONCAT('%', :phoneNumber, '%'))
            """, nativeQuery = true)
    Page<User> searchQuizTypeNameOrPhoneNumber(@Param("name") String name,
                                               @Param("phoneNumber") String phoneNumber,
                                               Pageable pageable);


    @Query(value = """
            select u.* from users u
            inner join user_progress up on up.user_id = u.id
            WHERE (:name IS NULL OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%'))
                  OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')))
             AND (:phoneNumber IS NULL OR u.phone_number LIKE CONCAT('%', :phoneNumber, '%'))
            """, nativeQuery = true)
    Page<User> searchOnlineTypeNameOrPhoneNumber(@Param("name") String name,
                                                 @Param("phoneNumber") String phoneNumber,
                                                 PageRequest pageRequest);


    @Query(value = "SELECT COUNT(DISTINCT categories_id) FROM users_categories WHERE user_id = ?1", nativeQuery = true)
    Integer countDistinctCategoriesByUserId(Long userId);

    @Query(value = "select count(*) from user_progress where user_id =?1", nativeQuery = true)
    Integer countByLesson(Long user);

    @Query(value = "SELECT * FROM users u WHERE (:keyword IS NOT NULL AND (LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%'))\n" +
            "            OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%'))))", nativeQuery = true)
    List<User> findUserByKeyword(@Param("keyword") String keyword);

    List<User> findAllByGroupId(Integer groupId);

}
