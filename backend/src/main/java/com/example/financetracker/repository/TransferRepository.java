package com.example.financetracker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.financetracker.entity.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByUserIdOrderByTransferredAtDesc(Long userId);
}
