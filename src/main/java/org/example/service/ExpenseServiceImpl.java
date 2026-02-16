package org.example.service;

import org.example.model.AppUser;
import org.example.model.Expense;
import org.example.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    // Constructor to inject the repository and userService
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    @Override
    public List<Expense> getAllUserExpenses(Long userId){
        return new ArrayList<>(expenseRepository.findByUserIdOrderByDateDesc(userId));
    }

    // Fetch all expenses for a specific date and user
    @Override
    public List<Expense> getExpensesByDay(String date, Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream()
                .filter(expense -> expense.getDate().equals(date))
                .collect(Collectors.toList());
    }

    // Fetch all expenses for a category within a specific month and user
    @Override
    public List<Expense> getExpensesByCategoryAndMonth(String category, String month, Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category)
                        && expense.getDate().startsWith(month))
                .collect(Collectors.toList());
    }

    // Get a list of all distinct expense categories for a user
    @Override
    public List<String> getAllExpenseCategories(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(Expense::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    // Retrieve an expense by its ID and user
    @Override
    public Optional<Expense> getExpenseById(Long id, Long userId) {
        return expenseRepository.findByIdAndUserId(id, userId);
    }

    // Add a new expense to the database for a user
    @Override
    public Expense addExpense(Expense expense, Long userId) {
        Optional<AppUser> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            expense.setUser(user);
            return expenseRepository.save(expense);
        } else {
            // Handle the case where the user is not found
            throw new RuntimeException("User not found");
        }
    }

    // Update an existing expense in the database for a user
    @Override
    public boolean updateExpense(Expense updatedExpense, Long userId) {
        Optional<Expense> existingExpense = expenseRepository.findByIdAndUserId(updatedExpense.getId(), userId);
        if (existingExpense.isPresent()) {
            updatedExpense.setUser(existingExpense.get().getUser());
            expenseRepository.save(updatedExpense);
            return true;
        }
        return false;
    }

    // Delete an expense by its ID and user
    @Override
    public boolean deleteExpense(Long id, Long userId) {
        Optional<Expense> existingExpense = expenseRepository.findByIdAndUserId(id, userId);
        if (existingExpense.isPresent()) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}


