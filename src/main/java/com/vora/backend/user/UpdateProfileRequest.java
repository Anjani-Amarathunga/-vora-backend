package com.vora.backend.user;

public record UpdateProfileRequest(
        String fullName,
        String phoneNumber,
        String address,
        String city,
        String state,
        String postalCode,
        String country
) {}
