package com.vora.backend.service.Admin_services.impl;


import com.vora.backend.product.dto.Admin_dto.request.AdminSettingsRequest;
import com.vora.backend.product.dto.Admin_dto.response.AdminSettingsResponse;
import com.vora.backend.user.Admin_entity.AdminSettings;
import com.vora.backend.repository.Admin_repository.AdminSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminSettingsService {

    private final AdminSettingsRepository adminSettingsRepository;

    @Transactional(readOnly = true)
    public AdminSettingsResponse getSettings() {
        AdminSettings settings = adminSettingsRepository.findAll()
                .stream().findFirst()
                .orElseGet(this::createDefaults);
        return toResponse(settings);
    }

    public AdminSettingsResponse updateSettings(AdminSettingsRequest req) {
        AdminSettings settings = adminSettingsRepository.findAll()
                .stream().findFirst()
                .orElseGet(this::createDefaults);

        settings.setStoreName(req.getStoreName());
        settings.setStoreEmail(req.getStoreEmail());
        settings.setCurrency(req.getCurrency());
        settings.setTimezone(req.getTimezone());
        settings.setLanguage(req.getLanguage());
        settings.setAddress(req.getAddress());
        settings.setLowStockThreshold(req.getLowStockThreshold());
        settings.setOrderPrefix(req.getOrderPrefix());
        settings.setNotifyNewOrders(req.getNotifyNewOrders());
        settings.setNotifyPaymentUpdates(req.getNotifyPaymentUpdates());
        settings.setNotifyLowStockAlerts(req.getNotifyLowStockAlerts());
        settings.setNotifyNewUsers(req.getNotifyNewUsers());
        settings.setNotifySystemUpdates(req.getNotifySystemUpdates());

        return toResponse(adminSettingsRepository.save(settings));
    }

    private AdminSettings createDefaults() {
        AdminSettings defaults = AdminSettings.builder()
                .storeName("Evora")
                .storeEmail("support@evora.com")
                .currency("USD")
                .timezone("Asia/Colombo")
                .language("English")
                .address("")
                .lowStockThreshold(10)
                .orderPrefix("EVR-")
                .notifyNewOrders(true)
                .notifyPaymentUpdates(true)
                .notifyLowStockAlerts(true)
                .notifyNewUsers(false)
                .notifySystemUpdates(false)
                .build();
        return adminSettingsRepository.save(defaults);
    }

    private AdminSettingsResponse toResponse(AdminSettings settings) {
        return AdminSettingsResponse.builder()
                .id(settings.getId())
                .storeName(settings.getStoreName())
                .storeEmail(settings.getStoreEmail())
                .currency(settings.getCurrency())
                .timezone(settings.getTimezone())
                .language(settings.getLanguage())
                .address(settings.getAddress())
                .lowStockThreshold(settings.getLowStockThreshold())
                .orderPrefix(settings.getOrderPrefix())
                .notifyNewOrders(settings.getNotifyNewOrders())
                .notifyPaymentUpdates(settings.getNotifyPaymentUpdates())
                .notifyLowStockAlerts(settings.getNotifyLowStockAlerts())
                .notifyNewUsers(settings.getNotifyNewUsers())
                .notifySystemUpdates(settings.getNotifySystemUpdates())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
