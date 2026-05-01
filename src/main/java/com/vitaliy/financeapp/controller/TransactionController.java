package com.vitaliy.financeapp.controller;

import com.vitaliy.financeapp.entity.Transaction;
import com.vitaliy.financeapp.entity.User;
import com.vitaliy.financeapp.repository.TransactionRepository;
import com.vitaliy.financeapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @PostMapping
    public Transaction addTransaction(@RequestParam Long userId,
                                      @RequestBody Transaction transaction) {

        User user = userRepository.findById(userId).orElseThrow();
        transaction.setUser(user);

        return transactionRepository.save(transaction);
    }

    @GetMapping
    public List<Transaction> getUserTransactions(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return transactionRepository.findByUser(user);
    }

    @GetMapping("/balance")
    public String getBalance(@RequestParam Long userId) {

        User user = userRepository.findById(userId).orElseThrow();

        Double income = transactionRepository.getTotalIncome(user);
        Double expense = transactionRepository.getTotalExpense(user);

        if (income == null) income = 0.0;
        if (expense == null) expense = 0.0;

        double balance = income - expense;

        return "Balance: " + balance +
                " (income=" + income + ", expense=" + expense + ")";
    }

}