package com.example.financetracker.service;

import java.math.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.financetracker.dto.FinanceDtos.*;
import com.example.financetracker.entity.*;
import com.example.financetracker.exception.ApiException;
import com.example.financetracker.repository.*;

@Service
@Transactional(readOnly = true)
public class FinanceService {
    private final AuthService auth;
    private final UserRepository users;
    private final AccountRepository accounts;
    private final CategoryRepository categories;
    private final IncomeRepository incomes;
    private final ExpenseRepository expenses;
    private final BudgetRepository budgets;
    private final SubscriptionRepository subscriptions;
    private final FinancialGoalRepository goals;
    private final TransferRepository transfers;

    public FinanceService(AuthService auth, UserRepository users, AccountRepository accounts,
            CategoryRepository categories,
            IncomeRepository incomes, ExpenseRepository expenses, BudgetRepository budgets,
            SubscriptionRepository subscriptions, FinancialGoalRepository goals, TransferRepository transfers) {
        this.auth = auth;
        this.users = users;
        this.accounts = accounts;
        this.categories = categories;
        this.incomes = incomes;
        this.expenses = expenses;
        this.budgets = budgets;
        this.subscriptions = subscriptions;
        this.goals = goals;
        this.transfers = transfers;
    }

    public List<Map<String, Object>> accounts(String email) {
        return accounts.findByUserIdOrderByCreatedAtAsc(user(email).getId()).stream().map(this::accountMap).toList();
    }

    @Transactional
    public Map<String, Object> saveAccount(String email, Long id, AccountRequest r) {
        User user = user(email);
        Account item = id == null ? new Account() : account(id, user.getId());
        item.setUser(user);
        item.setName(r.name());
        item.setType(upper(r.type(), "BANK"));
        item.setBalance(r.balance());
        item.setCurrency(text(r.currency(), "PHP"));
        item.setColor(text(r.color(), "#2F80ED"));
        item.setActive(r.active() == null || r.active());
        return accountMap(accounts.save(item));
    }

    @Transactional
    public void deleteAccount(String email, Long id) {
        Account item = account(id, user(email).getId());
        item.setActive(false);
        accounts.save(item);
    }

    public List<Map<String, Object>> categories(String email) {
        return categories.findAvailable(user(email).getId()).stream().map(this::categoryMap).toList();
    }

    @Transactional
    public Map<String, Object> saveCategory(String email, Long id, CategoryRequest r) {
        User user = user(email);
        Category item = id == null ? new Category() : ownedCategory(id, user.getId());
        item.setUser(user);
        item.setName(r.name());
        item.setType(upper(r.type(), "EXPENSE"));
        item.setColor(text(r.color(), "#2F80ED"));
        item.setIcon(text(r.icon(), "circle"));
        item.setSystemCategory(false);
        return categoryMap(categories.save(item));
    }

    @Transactional
    public void deleteCategory(String email, Long id) {
        categories.delete(ownedCategory(id, user(email).getId()));
    }

    public List<Map<String, Object>> incomes(String email) {
        return incomes.findByUserIdOrderByDateDescCreatedAtDesc(user(email).getId()).stream().map(this::incomeMap)
                .toList();
    }

    @Transactional
    public Map<String, Object> saveIncome(String email, Long id, IncomeRequest r) {
        User user = user(email);
        Income item = id == null ? new Income()
                : incomes.findByIdAndUserId(id, user.getId()).orElseThrow(() -> missing("Income"));
        if (id != null && item.getAccount() != null)
            adjust(item.getAccount(), item.getAmount().negate());
        Account account = optionalAccount(r.accountId(), user.getId());
        item.setUser(user);
        item.setSource(r.source());
        item.setAmount(r.amount());
        item.setDate(r.date());
        item.setFrequency(upper(r.frequency(), "ONE_TIME"));
        item.setCategory(optionalCategory(r.categoryId(), user.getId()));
        item.setAccount(account);
        item.setNotes(r.notes());
        if (account != null)
            adjust(account, r.amount());
        return incomeMap(incomes.save(item));
    }

    @Transactional
    public void deleteIncome(String email, Long id) {
        Income item = incomes.findByIdAndUserId(id, user(email).getId()).orElseThrow(() -> missing("Income"));
        if (item.getAccount() != null)
            adjust(item.getAccount(), item.getAmount().negate());
        incomes.delete(item);
    }

    public List<Map<String, Object>> expenses(String email) {
        return expenses.findByUserIdOrderByDateDescCreatedAtDesc(user(email).getId()).stream().map(this::expenseMap)
                .toList();
    }

    @Transactional
    public Map<String, Object> saveExpense(String email, Long id, ExpenseRequest r) {
        User user = user(email);
        Expense item = id == null ? new Expense()
                : expenses.findByIdAndUserId(id, user.getId()).orElseThrow(() -> missing("Expense"));
        if (id != null && item.getAccount() != null)
            adjust(item.getAccount(), item.getAmount());
        Account account = optionalAccount(r.accountId(), user.getId());
        item.setUser(user);
        item.setMerchant(r.merchant());
        item.setAmount(r.amount());
        item.setDate(r.date());
        item.setPaymentMethod(upper(r.paymentMethod(), "CARD"));
        item.setCategory(optionalCategory(r.categoryId(), user.getId()));
        item.setAccount(account);
        item.setNotes(r.notes());
        item.setTags(r.tags());
        if (account != null)
            adjust(account, r.amount().negate());
        return expenseMap(expenses.save(item));
    }

    @Transactional
    public void deleteExpense(String email, Long id) {
        Expense item = expenses.findByIdAndUserId(id, user(email).getId()).orElseThrow(() -> missing("Expense"));
        if (item.getAccount() != null)
            adjust(item.getAccount(), item.getAmount());
        expenses.delete(item);
    }

    public List<Map<String, Object>> budgets(String email, LocalDate requestedMonth) {
        User user = user(email);
        LocalDate month = firstDay(requestedMonth == null ? LocalDate.now() : requestedMonth);
        return budgets.findByUserIdAndMonth(user.getId(), month).stream().map(b -> budgetMap(b, user.getId())).toList();
    }

    @Transactional
    public Map<String, Object> saveBudget(String email, Long id, BudgetRequest r) {
        User user = user(email);
        Budget item = id == null ? new Budget()
                : budgets.findByIdAndUserId(id, user.getId()).orElseThrow(() -> missing("Budget"));
        item.setUser(user);
        item.setCategory(category(r.categoryId(), user.getId()));
        item.setMonth(firstDay(r.month()));
        item.setLimitAmount(r.limitAmount());
        return budgetMap(budgets.save(item), user.getId());
    }

    @Transactional
    public void deleteBudget(String email, Long id) {
        budgets.delete(budgets.findByIdAndUserId(id, user(email).getId()).orElseThrow(() -> missing("Budget")));
    }

    public List<Map<String, Object>> subscriptions(String email) {
        return subscriptions.findByUserIdOrderByNextBillingDateAsc(user(email).getId()).stream()
                .map(this::subscriptionMap).toList();
    }

    @Transactional
    public Map<String, Object> saveSubscription(String email, Long id, SubscriptionRequest r) {
        User user = user(email);
        Subscription item = id == null ? new Subscription()
                : subscriptions.findByIdAndUserId(id, user.getId()).orElseThrow(() -> missing("Subscription"));
        item.setUser(user);
        item.setName(r.name());
        item.setAmount(r.amount());
        item.setBillingCycle(upper(r.billingCycle(), "MONTHLY"));
        item.setNextBillingDate(r.nextBillingDate());
        item.setStatus(upper(r.status(), "ACTIVE"));
        item.setCategory(optionalCategory(r.categoryId(), user.getId()));
        item.setAccount(optionalAccount(r.accountId(), user.getId()));
        return subscriptionMap(subscriptions.save(item));
    }

    @Transactional
    public void deleteSubscription(String email, Long id) {
        subscriptions.delete(
                subscriptions.findByIdAndUserId(id, user(email).getId()).orElseThrow(() -> missing("Subscription")));
    }

    public List<Map<String, Object>> goals(String email) {
        return goals.findByUserIdOrderByDeadlineAsc(user(email).getId()).stream().map(this::goalMap).toList();
    }

    @Transactional
    public Map<String, Object> saveGoal(String email, Long id, GoalRequest r) {
        User user = user(email);
        FinancialGoal item = id == null ? new FinancialGoal()
                : goals.findByIdAndUserId(id, user.getId()).orElseThrow(() -> missing("Goal"));
        item.setUser(user);
        item.setName(r.name());
        item.setTargetAmount(r.targetAmount());
        item.setCurrentAmount(r.currentAmount());
        item.setDeadline(r.deadline());
        item.setColor(text(r.color(), "#27AE60"));
        item.setStatus(upper(r.status(), "ACTIVE"));
        return goalMap(goals.save(item));
    }

    @Transactional
    public void deleteGoal(String email, Long id) {
        goals.delete(goals.findByIdAndUserId(id, user(email).getId()).orElseThrow(() -> missing("Goal")));
    }

    public List<Map<String, Object>> transfers(String email) {
        return transfers.findByUserIdOrderByTransferredAtDesc(user(email).getId()).stream().map(this::transferMap)
                .toList();
    }

    @Transactional
    public Map<String, Object> createTransfer(String email, TransferRequest r) {
        User user = user(email);
        if (Objects.equals(r.sourceAccountId(), r.destinationAccountId()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Choose two different accounts.");
        Account source = account(r.sourceAccountId(), user.getId());
        Account destination = account(r.destinationAccountId(), user.getId());
        if (source.getBalance().compareTo(r.amount()) < 0)
            throw new ApiException(HttpStatus.BAD_REQUEST, "The source account has insufficient funds.");
        adjust(source, r.amount().negate());
        adjust(destination, r.amount());
        Transfer item = Transfer.builder().user(user).sourceAccount(source).destinationAccount(destination)
                .amount(r.amount())
                .transferredAt(r.transferredAt() == null ? LocalDateTime.now() : r.transferredAt()).notes(r.notes())
                .build();
        return transferMap(transfers.save(item));
    }

    public Map<String, Object> dashboard(String email) {
        User user = user(email);
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        LocalDate previousStart = start.minusMonths(1);
        LocalDate previousEnd = start.minusDays(1);
        BigDecimal income = incomes.totalBetween(user.getId(), start, end);
        BigDecimal expense = expenses.totalBetween(user.getId(), start, end);
        BigDecimal previousExpense = expenses.totalBetween(user.getId(), previousStart, previousEnd);
        BigDecimal balance = accounts.findByUserIdOrderByCreatedAtAsc(user.getId()).stream()
                .filter(a -> Boolean.TRUE.equals(a.getActive()))
                .map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Map<String, Object>> categoryData = expenses.totalsByCategory(user.getId(), start, end).stream()
                .map(row -> map(
                        "name", row[0], "value", row[1], "color", row[2]))
                .toList();
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate s = start.minusMonths(i);
            LocalDate e = s.plusMonths(1).minusDays(1);
            trend.add(map("month", s.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), "income",
                    incomes.totalBetween(user.getId(), s, e),
                    "expense", expenses.totalBetween(user.getId(), s, e)));
        }
        List<Map<String, Object>> recent = new ArrayList<>();
        incomes.findByUserIdOrderByDateDescCreatedAtDesc(user.getId()).stream().limit(4)
                .forEach(i -> recent.add(map("id", "i" + i.getId(), "name", i.getSource(), "category",
                        name(i.getCategory()), "date", i.getDate(), "amount", i.getAmount(), "type", "income")));
        expenses.findByUserIdOrderByDateDescCreatedAtDesc(user.getId()).stream().limit(6)
                .forEach(e -> recent.add(map("id", "e" + e.getId(), "name", e.getMerchant(), "category",
                        name(e.getCategory()), "date", e.getDate(), "amount", e.getAmount(), "type", "expense")));
        recent.sort(Comparator.comparing(v -> (LocalDate) v.get("date"), Comparator.reverseOrder()));
        BigDecimal change = previousExpense.signum() == 0 ? BigDecimal.ZERO
                : expense.subtract(previousExpense).multiply(BigDecimal.valueOf(100)).divide(previousExpense, 1,
                        RoundingMode.HALF_UP);
        return map("income", income, "expenses", expense, "net", income.subtract(expense), "accountBalance", balance,
                "expenseChange", change,
                "trend", trend, "categoryBreakdown", categoryData, "budgets", budgets(email, start),
                "recentTransactions", recent.stream().limit(7).toList(),
                "accounts", accounts(email), "upcomingSubscriptions", subscriptions(email).stream().limit(4).toList());
    }

    public Map<String, Object> reports(String email, Integer year) {
        User user = user(email);
        int selectedYear = year == null ? LocalDate.now().getYear() : year;
        List<Map<String, Object>> monthly = new ArrayList<>();
        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal expenseTotal = BigDecimal.ZERO;
        for (int month = 1; month <= 12; month++) {
            LocalDate start = LocalDate.of(selectedYear, month, 1);
            LocalDate end = start.plusMonths(1).minusDays(1);
            BigDecimal income = incomes.totalBetween(user.getId(), start, end);
            BigDecimal expense = expenses.totalBetween(user.getId(), start, end);
            incomeTotal = incomeTotal.add(income);
            expenseTotal = expenseTotal.add(expense);
            monthly.add(map("month", start.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), "income", income,
                    "expense", expense, "savings", income.subtract(expense)));
        }
        BigDecimal rate = incomeTotal.signum() == 0 ? BigDecimal.ZERO
                : incomeTotal.subtract(expenseTotal).multiply(BigDecimal.valueOf(100)).divide(incomeTotal, 1,
                        RoundingMode.HALF_UP);
        return map("year", selectedYear, "income", incomeTotal, "expenses", expenseTotal, "savings",
                incomeTotal.subtract(expenseTotal), "savingsRate", rate,
                "monthly", monthly, "categoryBreakdown",
                expenses.totalsByCategory(user.getId(), LocalDate.of(selectedYear, 1, 1),
                        LocalDate.of(selectedYear, 12, 31)).stream()
                        .map(row -> map("name", row[0], "value", row[1], "color", row[2])).toList());
    }

    @Transactional
    public Object updateProfile(String email, ProfileRequest r) {
        User user = user(email);
        user.setFirstName(r.firstName());
        user.setLastName(r.lastName());
        user.setPhoneNumber(r.phoneNumber());
        return auth.response(users.save(user));
    }

    private User user(String email) {
        return auth.requireUser(email);
    }

    private Account account(Long id, Long userId) {
        return accounts.findByIdAndUserId(id, userId).orElseThrow(() -> missing("Account"));
    }

    private Account optionalAccount(Long id, Long userId) {
        return id == null ? null : account(id, userId);
    }

    private Category optionalCategory(Long id, Long userId) {
        return id == null ? null : category(id, userId);
    }

    private Category category(Long id, Long userId) {
        Category item = categories.findById(id).orElseThrow(() -> missing("Category"));
        if (item.getUser() != null && !Objects.equals(item.getUser().getId(), userId))
            throw missing("Category");
        return item;
    }

    private Category ownedCategory(Long id, Long userId) {
        return categories.findByIdAndUserId(id, userId).orElseThrow(() -> missing("Category"));
    }

    private ApiException missing(String item) {
        return new ApiException(HttpStatus.NOT_FOUND, item + " not found.");
    }

    private void adjust(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accounts.save(account);
    }

    private LocalDate firstDay(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    private String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String upper(String value, String fallback) {
        return text(value, fallback).toUpperCase(Locale.ROOT);
    }

    private String name(Category category) {
        return category == null ? "Uncategorized" : category.getName();
    }

    private Map<String, Object> accountMap(Account a) {
        return map("id", a.getId(), "name", a.getName(), "type", a.getType(), "balance", a.getBalance(), "currency",
                a.getCurrency(), "color", a.getColor(), "active", a.getActive(), "updatedAt", a.getUpdatedAt());
    }

    private Map<String, Object> categoryMap(Category c) {
        return map("id", c.getId(), "name", c.getName(), "type", c.getType(), "color", c.getColor(), "icon",
                c.getIcon(), "system", c.getSystemCategory());
    }

    private Map<String, Object> incomeMap(Income i) {
        return map("id", i.getId(), "source", i.getSource(), "amount", i.getAmount(), "date", i.getDate(), "frequency",
                i.getFrequency(), "category", i.getCategory() == null ? null : categoryMap(i.getCategory()), "account",
                i.getAccount() == null ? null : accountMap(i.getAccount()), "notes", i.getNotes(), "createdAt",
                i.getCreatedAt());
    }

    private Map<String, Object> expenseMap(Expense e) {
        return map("id", e.getId(), "merchant", e.getMerchant(), "amount", e.getAmount(), "date", e.getDate(),
                "paymentMethod", e.getPaymentMethod(), "category",
                e.getCategory() == null ? null : categoryMap(e.getCategory()), "account",
                e.getAccount() == null ? null : accountMap(e.getAccount()), "notes", e.getNotes(), "tags", e.getTags(),
                "createdAt", e.getCreatedAt());
    }

    private Map<String, Object> budgetMap(Budget b, Long userId) {
        LocalDate end = b.getMonth().plusMonths(1).minusDays(1);
        BigDecimal spent = expenses.totalForCategory(userId, b.getCategory().getId(), b.getMonth(), end);
        BigDecimal percentage = b.getLimitAmount().signum() == 0 ? BigDecimal.ZERO
                : spent.multiply(BigDecimal.valueOf(100)).divide(b.getLimitAmount(), 1, RoundingMode.HALF_UP);
        return map("id", b.getId(), "category", categoryMap(b.getCategory()), "month", b.getMonth(), "limitAmount",
                b.getLimitAmount(), "spent", spent,
                "remaining", b.getLimitAmount().subtract(spent), "percentage", percentage);
    }

    private Map<String, Object> subscriptionMap(Subscription s) {
        return map("id", s.getId(), "name", s.getName(), "amount", s.getAmount(), "billingCycle", s.getBillingCycle(),
                "nextBillingDate", s.getNextBillingDate(), "status", s.getStatus(), "category",
                s.getCategory() == null ? null : categoryMap(s.getCategory()), "account",
                s.getAccount() == null ? null : accountMap(s.getAccount()));
    }

    private Map<String, Object> goalMap(FinancialGoal g) {
        BigDecimal p = g.getTargetAmount().signum() == 0 ? BigDecimal.ZERO
                : g.getCurrentAmount().multiply(BigDecimal.valueOf(100)).divide(g.getTargetAmount(), 1,
                        RoundingMode.HALF_UP);
        return map("id", g.getId(), "name", g.getName(), "targetAmount", g.getTargetAmount(), "currentAmount",
                g.getCurrentAmount(), "deadline", g.getDeadline(), "color", g.getColor(), "status", g.getStatus(),
                "percentage", p);
    }

    private Map<String, Object> transferMap(Transfer t) {
        return map("id", t.getId(), "sourceAccount", accountMap(t.getSourceAccount()), "destinationAccount",
                accountMap(t.getDestinationAccount()), "amount", t.getAmount(), "transferredAt", t.getTransferredAt(),
                "notes", t.getNotes());
    }

    private Map<String, Object> map(Object... values) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2)
            map.put((String) values[i], values[i + 1]);
        return map;
    }
}
