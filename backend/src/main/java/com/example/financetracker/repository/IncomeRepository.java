package com.example.financetracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.financetracker.entity.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserIdOrderByDateDescCreatedAtDesc(Long userId);

    Optional<Income> findByIdAndUserId(Long id, Long userId);

    @Query("select coalesce(sum(i.amount), 0) from Income i where i.user.id=:userId and i.date between :start and :end")
    BigDecimal totalBetween(Long userId, LocalDate start, LocalDate end);
}
