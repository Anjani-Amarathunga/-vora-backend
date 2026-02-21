package com.vora.backend.config;

import com.vora.backend.user.Role;
import com.vora.backend.user.User;
import com.vora.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@vora.com").isEmpty()) {
            userRepository.save(User.builder()
                    .fullName("Admin User")
                    .email("admin@vora.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(Role.ADMIN))
                    .build());
        }

        if (userRepository.findByEmail("user@vora.com").isEmpty()) {
            userRepository.save(User.builder()
                    .fullName("Customer User")
                    .email("user@vora.com")
                    .password(passwordEncoder.encode("user123"))
                    .roles(Set.of(Role.CUSTOMER))
                    .build());
        }
    }
}
