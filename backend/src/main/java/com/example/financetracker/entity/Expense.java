package com.example.financetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "expense_transactions", indexes = {
        @Index(name = "idx_expense_user_date", columnList = "user_id,expense_date"),
        @Index(name = "idx_expense_category", columnList = "category_id") })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    @Column(nullable = false, length = 120)
    private String merchant;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Column(name = "expense_date", nullable = false)
    private LocalDate date;
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;
    @Column(columnDefinition = "TEXT")
    private String notes;
    @Column(length = 255)
    private String tags;
}
