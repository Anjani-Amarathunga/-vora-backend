package com.vora.backend.service.Admin_services.impl;

import com.vora.backend.product.dto.Admin_dto.response.AdminNotificationResponse;
import com.vora.backend.user.Admin_entity.AdminNotification;
import com.vora.backend.exception.ResourceNotFoundException;
import com.vora.backend.repository.Admin_repository.AdminNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminNotificationService {

    private final AdminNotificationRepository adminNotificationRepository;

    @Transactional(readOnly = true)
    public List<AdminNotificationResponse> getAll() {
        return adminNotificationRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return adminNotificationRepository.countByReadFalse();
    }

    public AdminNotificationResponse create(String type, String title, String message) {
        AdminNotification notification = AdminNotification.builder()
                .type(type)
                .title(title)
                .message(message)
                .build();
        return toResponse(adminNotificationRepository.save(notification));
    }

    public AdminNotificationResponse markAsRead(Long id) {
        AdminNotification notification = adminNotificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        return toResponse(adminNotificationRepository.save(notification));
    }

    public void markAllAsRead() {
        List<AdminNotification> all = adminNotificationRepository.findAllByOrderByCreatedAtDesc();
        all.forEach(n -> n.setRead(true));
        adminNotificationRepository.saveAll(all);
    }

    public void delete(Long id) {
        if (!adminNotificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found");
        }
        adminNotificationRepository.deleteById(id);
    }

    public void clearAll() {
        adminNotificationRepository.deleteAll();
    }

    private AdminNotificationResponse toResponse(AdminNotification notification) {
        return AdminNotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .metaJson(notification.getMetaJson())
                .read(notification.getRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
