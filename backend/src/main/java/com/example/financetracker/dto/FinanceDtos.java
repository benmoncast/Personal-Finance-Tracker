package com.example.financetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class FinanceDtos {
    private FinanceDtos() {
    }

    public record AccountRequest(@NotBlank String name, @NotBlank String type,
            @NotNull @DecimalMin("0") BigDecimal balance,
            String currency, String color, Boolean active) {
    }

    public record CategoryRequest(@NotBlank String name, @NotBlank String type, String color, String icon) {
    }

    public record IncomeRequest(@NotBlank String source, @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotNull LocalDate date, String frequency, Long categoryId, Long accountId, String notes) {
    }

    public record ExpenseRequest(@NotBlank String merchant, @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotNull LocalDate date, String paymentMethod, Long categoryId, Long accountId,
            String notes, String tags) {
    }

    public record BudgetRequest(@NotNull Long categoryId, @NotNull LocalDate month,
            @NotNull @DecimalMin("0.01") BigDecimal limitAmount) {
    }

    public record SubscriptionRequest(@NotBlank String name, @NotNull @DecimalMin("0.01") BigDecimal amount,
            String billingCycle, @NotNull LocalDate nextBillingDate, String status,
            Long categoryId, Long accountId) {
    }

    public record GoalRequest(@NotBlank String name, @NotNull @DecimalMin("0.01") BigDecimal targetAmount,
            @NotNull @DecimalMin("0") BigDecimal currentAmount, LocalDate deadline,
            String color, String status) {
    }

    public record TransferRequest(@NotNull Long sourceAccountId, @NotNull Long destinationAccountId,
            @NotNull @DecimalMin("0.01") BigDecimal amount, LocalDateTime transferredAt, String notes) {
    }

    public record ProfileRequest(@NotBlank String firstName, @NotBlank String lastName, String phoneNumber) {
    }
}
