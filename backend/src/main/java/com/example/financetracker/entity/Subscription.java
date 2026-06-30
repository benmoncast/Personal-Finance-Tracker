package com.example.financetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "subscriptions", indexes = { @Index(name = "idx_subscription_user", columnList = "user_id"),
        @Index(name = "idx_subscription_next_billing", columnList = "next_billing_date") })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends BaseEntity {
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
    private String name;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Column(name = "billing_cycle", nullable = false, length = 20)
    private String billingCycle;
    @Column(name = "next_billing_date", nullable = false)
    private LocalDate nextBillingDate;
    @Column(nullable = false, length = 20)
    private String status;
}
