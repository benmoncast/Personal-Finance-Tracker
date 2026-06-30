package com.example.financetracker.controller;

import java.security.Principal;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.example.financetracker.entity.User;
import com.example.financetracker.exception.ApiException;
import com.example.financetracker.repository.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users;
    private final IncomeRepository incomes;
    private final ExpenseRepository expenses;
    private final AccountRepository accounts;
    private final SubscriptionRepository subscriptions;

    public AdminController(UserRepository users, IncomeRepository incomes, ExpenseRepository expenses,
            AccountRepository accounts, SubscriptionRepository subscriptions) {
        this.users = users;
        this.incomes = incomes;
        this.expenses = expenses;
        this.accounts = accounts;
        this.subscriptions = subscriptions;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of("users", users.count(), "incomeRecords", incomes.count(), "expenseRecords", expenses.count(),
                "accounts", accounts.count(), "subscriptions", subscriptions.count());
    }

    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return users.findAll().stream()
                .map(u -> Map.<String, Object>of("id", u.getId(), "name", u.getFirstName() + " " + u.getLastName(),
                        "email", u.getEmail(), "role", u.getRole(), "status", u.getStatus(), "joined",
                        u.getCreatedAt()))
                .toList();
    }

    @PatchMapping("/users/{id}/status")
    public void status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = users.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
        user.setStatus("disabled".equalsIgnoreCase(body.get("status")) ? "disabled" : "active");
        users.save(user);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id, Principal principal) {
        User user = users.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
        if (user.getEmail().equalsIgnoreCase(principal.getName()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "You cannot delete your own administrator account.");
        users.delete(user);
    }
}
