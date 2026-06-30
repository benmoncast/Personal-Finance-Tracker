package com.example.financetracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.financetracker.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByDateDescCreatedAtDesc(Long userId);

    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.user.id=:userId and e.date between :start and :end")
    BigDecimal totalBetween(Long userId, LocalDate start, LocalDate end);

    @Query("select e.category.name, coalesce(sum(e.amount),0), e.category.color from Expense e where e.user.id=:userId and e.date between :start and :end group by e.category.id, e.category.name, e.category.color order by sum(e.amount) desc")
    List<Object[]> totalsByCategory(Long userId, LocalDate start, LocalDate end);

    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.user.id=:userId and e.category.id=:categoryId and e.date between :start and :end")
    BigDecimal totalForCategory(Long userId, Long categoryId, LocalDate start, LocalDate end);
}
