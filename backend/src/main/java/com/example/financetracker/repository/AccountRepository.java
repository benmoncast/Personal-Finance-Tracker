package com.example.financetracker.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financetracker.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserIdOrderByCreatedAtAsc(Long userId);

    Optional<Account> findByIdAndUserId(Long id, Long userId);
}
