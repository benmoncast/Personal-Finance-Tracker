package com.example.financetracker.config;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.financetracker.entity.*;
import com.example.financetracker.repository.*;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(@Value("${app.seed.enabled:true}") boolean enabled, UserRepository users,
            CategoryRepository categories,
            AccountRepository accounts, IncomeRepository incomes, ExpenseRepository expenses, BudgetRepository budgets,
            SubscriptionRepository subscriptions, FinancialGoalRepository goals, PasswordEncoder encoder) {
        return args -> {
            if (enabled)
                createDemo(users, categories, accounts, incomes, expenses, budgets, subscriptions, goals, encoder);
        };
    }

    @Transactional
    void createDemo(UserRepository users, CategoryRepository categories, AccountRepository accounts,
            IncomeRepository incomes,
            ExpenseRepository expenses, BudgetRepository budgets, SubscriptionRepository subscriptions,
            FinancialGoalRepository goals, PasswordEncoder encoder) {
        Map<String, Category> categoryMap = ensureCategories(categories);
        if (users.findByEmailIgnoreCase("admin@fintrack.app").isEmpty()) {
            users.save(User.builder().firstName("Finance").lastName("Admin").username("financeadmin")
                    .email("admin@fintrack.app")
                    .password(encoder.encode("Admin1234!")).role("ADMIN").status("active").build());
        }
        if (users.findByEmailIgnoreCase("demo@fintrack.app").isPresent())
            return;
        User user = users.save(
                User.builder().firstName("Alex").lastName("Morgan").username("alexmorgan").email("demo@fintrack.app")
                        .password(encoder.encode("Demo1234!")).role("USER").status("active")
                        .phoneNumber("+63 917 555 0134").build());
        Account bank = accounts.save(Account.builder().user(user).name("Main checking").type("BANK")
                .balance(new BigDecimal("84750.00")).currency("PHP").color("#2F80ED").active(true).build());
        Account wallet = accounts.save(Account.builder().user(user).name("Daily wallet").type("E_WALLET")
                .balance(new BigDecimal("8420.00")).currency("PHP").color("#8B5CF6").active(true).build());
        Account cash = accounts.save(Account.builder().user(user).name("Cash").type("CASH")
                .balance(new BigDecimal("3500.00")).currency("PHP").color("#27AE60").active(true).build());
        LocalDate today = LocalDate.now();
        LocalDate month = today.withDayOfMonth(1);
        incomes.save(Income.builder().user(user).account(bank).category(categoryMap.get("Salary"))
                .source("Acme Studio payroll").amount(new BigDecimal("65000")).date(month.plusDays(4))
                .frequency("MONTHLY").notes("Monthly salary").build());
        incomes.save(Income.builder().user(user).account(wallet).category(categoryMap.get("Freelance"))
                .source("Brand identity project").amount(new BigDecimal("18500")).date(month.plusDays(11))
                .frequency("ONE_TIME").notes("Client milestone").build());
        Object[][] samples = { { "The Marketplace", "Groceries", "4280", 6, bank },
                { "City apartment", "Housing", "18000", 1, bank }, { "Grab", "Transport", "680", 13, wallet },
                { "Electric utility", "Bills", "2340", 8, bank }, { "Morning Brew", "Dining", "560", 15, wallet },
                { "Design conference", "Lifestyle", "3200", 18, bank } };
        for (Object[] row : samples)
            expenses.save(Expense.builder().user(user).merchant((String) row[0]).category(categoryMap.get(row[1]))
                    .amount(new BigDecimal((String) row[2]))
                    .date(month.plusDays(Math.min((Integer) row[3], month.lengthOfMonth() - 1))).paymentMethod("CARD")
                    .account((Account) row[4]).tags("sample").build());
        for (String name : List.of("Groceries", "Housing", "Transport", "Bills", "Dining", "Lifestyle")) {
            BigDecimal limit = switch (name) {
                case "Housing" -> new BigDecimal("22000");
                case "Groceries" -> new BigDecimal("9000");
                case "Lifestyle" -> new BigDecimal("6000");
                default -> new BigDecimal("5000");
            };
            budgets.save(Budget.builder().user(user).category(categoryMap.get(name)).month(month).limitAmount(limit)
                    .build());
        }
        subscriptions.save(Subscription.builder().user(user).name("Netflix").amount(new BigDecimal("549"))
                .billingCycle("MONTHLY").nextBillingDate(today.plusDays(4)).status("ACTIVE")
                .category(categoryMap.get("Lifestyle")).account(bank).build());
        subscriptions.save(Subscription.builder().user(user).name("Spotify").amount(new BigDecimal("149"))
                .billingCycle("MONTHLY").nextBillingDate(today.plusDays(9)).status("ACTIVE")
                .category(categoryMap.get("Lifestyle")).account(wallet).build());
        goals.save(FinancialGoal.builder().user(user).name("Emergency fund").targetAmount(new BigDecimal("180000"))
                .currentAmount(new BigDecimal("112000")).deadline(today.plusMonths(8)).color("#27AE60").status("ACTIVE")
                .build());
        goals.save(FinancialGoal.builder().user(user).name("Japan trip").targetAmount(new BigDecimal("90000"))
                .currentAmount(new BigDecimal("34500")).deadline(today.plusMonths(5)).color("#8B5CF6").status("ACTIVE")
                .build());
    }

    private Map<String, Category> ensureCategories(CategoryRepository repository) {
        Map<String, Category> result = new HashMap<>();
        repository.findAll().stream().filter(c -> c.getUser() == null).forEach(c -> result.put(c.getName(), c));
        Object[][] defaults = { { "Salary", "INCOME", "#27AE60", "briefcase" },
                { "Freelance", "INCOME", "#2F80ED", "laptop" }, { "Business", "INCOME", "#8B5CF6", "building" },
                { "Groceries", "EXPENSE", "#F59E0B", "shopping-basket" }, { "Housing", "EXPENSE", "#2F80ED", "house" },
                { "Transport", "EXPENSE", "#8B5CF6", "car" }, { "Bills", "EXPENSE", "#EB5757", "receipt" },
                { "Dining", "EXPENSE", "#F97316", "utensils" }, { "Lifestyle", "EXPENSE", "#EC4899", "sparkles" },
                { "Health", "EXPENSE", "#14B8A6", "heart" } };
        for (Object[] row : defaults)
            if (!result.containsKey(row[0])) {
                Category saved = repository.save(Category.builder().name((String) row[0]).type((String) row[1])
                        .color((String) row[2]).icon((String) row[3]).systemCategory(true).build());
                result.put(saved.getName(), saved);
            }
        return result;
    }
}
