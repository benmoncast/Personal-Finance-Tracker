package com.example.financetracker.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financetracker.entity.FinancialGoal;

public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    List<FinancialGoal> findByUserIdOrderByDeadlineAsc(Long userId);

    Optional<FinancialGoal> findByIdAndUserId(Long id, Long userId);
}
