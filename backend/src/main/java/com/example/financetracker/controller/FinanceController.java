package com.example.financetracker.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.example.financetracker.dto.FinanceDtos.*;
import com.example.financetracker.service.FinanceService;

@RestController
@RequestMapping("/api")
public class FinanceController {
    private final FinanceService finance;

    public FinanceController(FinanceService finance) {
        this.finance = finance;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(Principal p) {
        return finance.dashboard(p.getName());
    }

    @GetMapping("/reports")
    public Map<String, Object> reports(Principal p, @RequestParam(required = false) Integer year) {
        return finance.reports(p.getName(), year);
    }

    @GetMapping("/accounts")
    public List<Map<String, Object>> accounts(Principal p) {
        return finance.accounts(p.getName());
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addAccount(Principal p, @Valid @RequestBody AccountRequest r) {
        return finance.saveAccount(p.getName(), null, r);
    }

    @PutMapping("/accounts/{id}")
    public Map<String, Object> editAccount(Principal p, @PathVariable Long id, @Valid @RequestBody AccountRequest r) {
        return finance.saveAccount(p.getName(), id, r);
    }

    @DeleteMapping("/accounts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAccount(Principal p, @PathVariable Long id) {
        finance.deleteAccount(p.getName(), id);
    }

    @GetMapping("/categories")
    public List<Map<String, Object>> categories(Principal p) {
        return finance.categories(p.getName());
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addCategory(Principal p, @Valid @RequestBody CategoryRequest r) {
        return finance.saveCategory(p.getName(), null, r);
    }

    @PutMapping("/categories/{id}")
    public Map<String, Object> editCategory(Principal p, @PathVariable Long id, @Valid @RequestBody CategoryRequest r) {
        return finance.saveCategory(p.getName(), id, r);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(Principal p, @PathVariable Long id) {
        finance.deleteCategory(p.getName(), id);
    }

    @GetMapping("/incomes")
    public List<Map<String, Object>> incomes(Principal p) {
        return finance.incomes(p.getName());
    }

    @PostMapping("/incomes")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addIncome(Principal p, @Valid @RequestBody IncomeRequest r) {
        return finance.saveIncome(p.getName(), null, r);
    }

    @PutMapping("/incomes/{id}")
    public Map<String, Object> editIncome(Principal p, @PathVariable Long id, @Valid @RequestBody IncomeRequest r) {
        return finance.saveIncome(p.getName(), id, r);
    }

    @DeleteMapping("/incomes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeIncome(Principal p, @PathVariable Long id) {
        finance.deleteIncome(p.getName(), id);
    }

    @GetMapping("/expenses")
    public List<Map<String, Object>> expenses(Principal p) {
        return finance.expenses(p.getName());
    }

    @PostMapping("/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addExpense(Principal p, @Valid @RequestBody ExpenseRequest r) {
        return finance.saveExpense(p.getName(), null, r);
    }

    @PutMapping("/expenses/{id}")
    public Map<String, Object> editExpense(Principal p, @PathVariable Long id, @Valid @RequestBody ExpenseRequest r) {
        return finance.saveExpense(p.getName(), id, r);
    }

    @DeleteMapping("/expenses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeExpense(Principal p, @PathVariable Long id) {
        finance.deleteExpense(p.getName(), id);
    }

    @GetMapping("/budgets")
    public List<Map<String, Object>> budgets(Principal p, @RequestParam(required = false) LocalDate month) {
        return finance.budgets(p.getName(), month);
    }

    @PostMapping("/budgets")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addBudget(Principal p, @Valid @RequestBody BudgetRequest r) {
        return finance.saveBudget(p.getName(), null, r);
    }

    @PutMapping("/budgets/{id}")
    public Map<String, Object> editBudget(Principal p, @PathVariable Long id, @Valid @RequestBody BudgetRequest r) {
        return finance.saveBudget(p.getName(), id, r);
    }

    @DeleteMapping("/budgets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBudget(Principal p, @PathVariable Long id) {
        finance.deleteBudget(p.getName(), id);
    }

    @GetMapping("/subscriptions")
    public List<Map<String, Object>> subscriptions(Principal p) {
        return finance.subscriptions(p.getName());
    }

    @PostMapping("/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addSubscription(Principal p, @Valid @RequestBody SubscriptionRequest r) {
        return finance.saveSubscription(p.getName(), null, r);
    }

    @PutMapping("/subscriptions/{id}")
    public Map<String, Object> editSubscription(Principal p, @PathVariable Long id,
            @Valid @RequestBody SubscriptionRequest r) {
        return finance.saveSubscription(p.getName(), id, r);
    }

    @DeleteMapping("/subscriptions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSubscription(Principal p, @PathVariable Long id) {
        finance.deleteSubscription(p.getName(), id);
    }

    @GetMapping("/goals")
    public List<Map<String, Object>> goals(Principal p) {
        return finance.goals(p.getName());
    }

    @PostMapping("/goals")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addGoal(Principal p, @Valid @RequestBody GoalRequest r) {
        return finance.saveGoal(p.getName(), null, r);
    }

    @PutMapping("/goals/{id}")
    public Map<String, Object> editGoal(Principal p, @PathVariable Long id, @Valid @RequestBody GoalRequest r) {
        return finance.saveGoal(p.getName(), id, r);
    }

    @DeleteMapping("/goals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGoal(Principal p, @PathVariable Long id) {
        finance.deleteGoal(p.getName(), id);
    }

    @GetMapping("/transfers")
    public List<Map<String, Object>> transfers(Principal p) {
        return finance.transfers(p.getName());
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addTransfer(Principal p, @Valid @RequestBody TransferRequest r) {
        return finance.createTransfer(p.getName(), r);
    }

    @PutMapping("/profile")
    public Object profile(Principal p, @Valid @RequestBody ProfileRequest r) {
        return finance.updateProfile(p.getName(), r);
    }
}
