package org.example.controller;

import org.example.model.AppUser;
import org.example.model.Expense;
import org.example.service.ExpenseService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;

    // Constructor injection
    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    // Get all expenses for the authenticated user
    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        List<Expense> expenses = expenseService.getAllUserExpenses(user.getId());
        return ResponseEntity.ok(expenses);
    }

    // Get all expenses by date for the authenticated user
    @GetMapping("/day/{date}")
    public ResponseEntity<List<Expense>> getExpensesByDay(@PathVariable String date, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        List<Expense> expenses = expenseService.getExpensesByDay(date, user.getId());
        return ResponseEntity.ok(expenses);
    }

    // Get all expenses for a particular category in a given month
    @GetMapping("/category/{category}/month")
    public ResponseEntity<List<Expense>> getExpensesByCategoryAndMonth(@PathVariable String category,
                                                                       @RequestParam String month, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        List<Expense> expenses = expenseService.getExpensesByCategoryAndMonth(category, month, user.getId());
        if (expenses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    // Get all expense categories for the authenticated user
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllExpenseCategories(Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        List<String> categories = expenseService.getAllExpenseCategories(user.getId());
        return ResponseEntity.ok(categories);
    }

    // Create a new expense for the authenticated user
    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        Expense savedExpense = expenseService.addExpense(expense, user.getId());
        return ResponseEntity.ok(savedExpense);
    }

    // Update an existing expense
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense expenseDetails, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        expenseDetails.setId(id);
        boolean isUpdated = expenseService.updateExpense(expenseDetails, user.getId());

        if (isUpdated) {
            return ResponseEntity.ok(expenseDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete an expense
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsername(username);
        boolean isDeleted = expenseService.deleteExpense(id, user.getId());

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}