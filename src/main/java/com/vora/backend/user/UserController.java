package com.vora.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final com.vora.backend.user.UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "fullName", user.getFullName(),
                "email", user.getEmail(),
                "phoneNumber", user.getPhoneNumber(),
                "address", user.getAddress(),
                "city", user.getCity(),
                "state", user.getState(),
                "postalCode", user.getPostalCode(),
                "country", user.getCountry(),
                "roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())));
    }

    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest req) {

        User u = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (req.fullName() != null)
            u.setFullName(req.fullName());
        if (req.phoneNumber() != null)
            u.setPhoneNumber(req.phoneNumber());
        if (req.address() != null)
            u.setAddress(req.address());
        if (req.city() != null)
            u.setCity(req.city());
        if (req.state() != null)
            u.setState(req.state());
        if (req.postalCode() != null)
            u.setPostalCode(req.postalCode());
        if (req.country() != null)
            u.setCountry(req.country());

        u.setUpdatedAt(LocalDateTime.now());
        userRepository.save(u);

        return ResponseEntity.ok(Map.of("message", "Profile updated"));
    }
}
