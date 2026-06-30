package com.example.financetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories", indexes = { @Index(name = "idx_categories_user", columnList = "user_id"),
        @Index(name = "idx_categories_type", columnList = "type") })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    @Column(nullable = false, length = 80)
    private String name;
    @Column(nullable = false, length = 20)
    private String type;
    @Column(length = 20)
    private String color;
    @Column(length = 50)
    private String icon;
    @Column(nullable = false)
    private Boolean systemCategory;
}
