package com.vitaliy.financeapp.controller;

import com.vitaliy.financeapp.entity.Transaction;
import com.vitaliy.financeapp.entity.User;
import com.vitaliy.financeapp.repository.TransactionRepository;
import com.vitaliy.financeapp.repository.UserRepository;
import java.util.ArrayList;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "1") Long userId, Model model) {

        User user = userRepository.findById(userId).orElseThrow();

        List<Transaction> transactions =
                transactionRepository.findByUserOrderByDateDescIdDesc(user);

        Double income = transactionRepository.getTotalIncome(user);
        Double expense = transactionRepository.getTotalExpense(user);

        if (income == null) income = 0.0;
        if (expense == null) expense = 0.0;

        double balance = income - expense;

        model.addAttribute("transactions", transactions);
        model.addAttribute("balance", balance);
        model.addAttribute("income", income);
        model.addAttribute("expense", expense);
        model.addAttribute("userId", userId);
        model.addAttribute("categories", categories);

        return "index";
    }

    @PostMapping("/add-category")
    public String addCategory(@RequestParam String newCategory) {

        if (newCategory != null && !newCategory.isBlank()) {
            categories.add(newCategory.toUpperCase());
        }

        return "redirect:/";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long userId,
                      @RequestParam Double amount,
                      @RequestParam String type,
                      @RequestParam(required = false) String description,
                      @RequestParam String category) {

        User user = userRepository.findById(userId).orElseThrow();

        Transaction t = new Transaction();
        t.setAmount(amount);
        t.setType(type);
        t.setDescription(description);
        t.setCategory(category);
        t.setDate(java.time.LocalDateTime.now());
        t.setUser(user);

        transactionRepository.save(t);

        return "redirect:/?userId=" + userId;
    }
    private List<String> categories = new ArrayList<>(
            List.of("SALARY", "BONUS", "FREELANCE")
    );
}