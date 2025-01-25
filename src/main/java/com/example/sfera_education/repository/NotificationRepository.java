package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.sfera_education.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Integer countAllByUserIdAndReadFalse(Long userId);

    List<Notification> findAllByUserId(Long userId);

    Notification findByRegistrantId(Long userId);
}
