package com.example.financetracker.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.financetracker.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select c from Category c where c.user.id = :userId or c.user is null order by c.type, c.name")
    List<Category> findAvailable(Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    long countBySystemCategoryTrue();
}
