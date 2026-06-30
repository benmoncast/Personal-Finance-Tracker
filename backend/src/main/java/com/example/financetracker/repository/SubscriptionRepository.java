package com.example.financetracker.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financetracker.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Subscription> findByUserIdOrderByNextBillingDateAsc(Long userId);

    Optional<Subscription> findByIdAndUserId(Long id, Long userId);
}
