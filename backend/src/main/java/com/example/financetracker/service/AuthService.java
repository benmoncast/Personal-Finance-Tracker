package com.example.financetracker.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.financetracker.dto.AuthDtos.*;
import com.example.financetracker.entity.User;
import com.example.financetracker.exception.ApiException;
import com.example.financetracker.repository.UserRepository;
import com.example.financetracker.security.JwtService;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authentication;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authentication,
            JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authentication = authentication;
        this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (users.existsByEmail(request.email()))
            throw new ApiException(HttpStatus.CONFLICT, "That email is already registered.");
        if (users.existsByUsername(request.username()))
            throw new ApiException(HttpStatus.CONFLICT, "That username is already taken.");
        User user = User.builder().firstName(request.firstName()).lastName(request.lastName())
                .username(request.username())
                .email(request.email().toLowerCase()).password(encoder.encode(request.password())).role("USER")
                .status("active").build();
        users.save(user);
        return new AuthResponse(jwt.generate(user), response(user));
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authentication.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (AuthenticationException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        User user = users.findByEmailIgnoreCase(request.email()).orElseThrow();
        return new AuthResponse(jwt.generate(user), response(user));
    }

    public User requireUser(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Authentication required."));
    }

    public UserResponse response(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getEmail(),
                user.getRole(), user.getStatus(), user.getPhoneNumber(), "PHP");
    }
}
