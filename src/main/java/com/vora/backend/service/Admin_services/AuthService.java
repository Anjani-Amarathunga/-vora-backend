package com.vora.backend.service.Admin_services;

import com.vora.backend.product.dto.Admin_dto.request.LoginRequest;
import com.vora.backend.product.dto.Admin_dto.request.RegisterRequest;
import com.vora.backend.product.dto.Admin_dto.response.AuthResponse;
import com.vora.backend.user.Admin_entity.Role;
import com.vora.backend.user.Admin_entity.User;
import com.vora.backend.exception.BadRequestException;
import com.vora.backend.repository.Admin_repository.RoleRepository;
import com.vora.backend.repository.Admin_repository.UserRepository;
import com.vora.backend.security.Admin_security.JwtUtil;
import com.vora.backend.service.Admin_services.impl.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AdminNotificationService adminNotificationService;

    public AuthResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        return buildResponse(token, user);
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BadRequestException("Email already in use");

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .isActive(true)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        adminNotificationService.create(
            "user",
            "New User Registered",
            user.getName() + " created a new account"
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return buildResponse(token, user);
    }

    private AuthResponse buildResponse(String token, User user) {
        AuthResponse res = new AuthResponse();
        res.setToken(token);
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setRoles(user.getRoles().stream()
                .map(Role::getName).collect(Collectors.toList()));
        return res;
    }
}
