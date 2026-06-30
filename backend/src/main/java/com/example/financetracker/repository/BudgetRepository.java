package com.example.financetracker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financetracker.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Budget> findByUserIdOrderByMonthDesc(Long userId);

    List<Budget> findByUserIdAndMonth(Long userId, LocalDate month);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);
}
