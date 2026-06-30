package com.example.financetracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
        private AuthDtos() {
        }

        public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
        }

        public record RegisterRequest(
                        @NotBlank String firstName,
                        @NotBlank String lastName,
                        @NotBlank @Size(min = 3, max = 100) String username,
                        @NotBlank @Email String email,
                        @NotBlank @Size(min = 8) String password) {
        }

        public record AuthResponse(String token, UserResponse user) {
        }

        public record UserResponse(Long id, String firstName, String lastName, String username, String email,
                        String role, String status, String phoneNumber, String currency) {
        }
}
