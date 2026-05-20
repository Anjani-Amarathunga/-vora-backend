package com.vora.backend.repository.Admin_repository;


import com.vora.backend.user.Admin_entity.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminSettingsRepository extends JpaRepository<AdminSettings, Long> {
}

