package com.vora.backend.auth;

import com.vora.backend.auth.dto.AuthResponse;
import com.vora.backend.auth.dto.LoginRequest;
import com.vora.backend.auth.dto.RegisterRequest;
import com.vora.backend.security.JwtService;
import com.vora.backend.user.Role;
import com.vora.backend.user.User;
import com.vora.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(Role.CUSTOMER))
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return new AuthResponse(token, savedUser.getEmail(), mapRoles(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), mapRoles(user));
    }

    private Set<String> mapRoles(User user) {
        return user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
    }
}
