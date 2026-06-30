package com.example.financetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "income_records", indexes = { @Index(name = "idx_income_user_date", columnList = "user_id,income_date"),
        @Index(name = "idx_income_category", columnList = "category_id") })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income extends BaseEntity {
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
    private String source;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Column(name = "income_date", nullable = false)
    private LocalDate date;
    @Column(nullable = false, length = 30)
    private String frequency;
    @Column(columnDefinition = "TEXT")
    private String notes;
}
