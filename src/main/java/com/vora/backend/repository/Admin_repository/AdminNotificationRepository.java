package com.vora.backend.repository.Admin_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vora.backend.user.Admin_entity.AdminNotification;

import java.util.List;

public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {
    List<AdminNotification> findAllByOrderByCreatedAtDesc();
    long countByReadFalse();
}
