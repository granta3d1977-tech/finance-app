package com.vitaliy.financeapp.controller;

import lombok.RequiredArgsConstructor;

import com.vitaliy.financeapp.entity.Transaction;
import com.vitaliy.financeapp.entity.User;
import com.vitaliy.financeapp.repository.TransactionRepository;
import com.vitaliy.financeapp.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<String> incomeCategories = List.of("SALARY", "BONUS", "FREELANCE");
    private final List<String> expenseCategories = List.of("FOOD", "TRANSPORT", "SHOPPING", "OTHER");

    private static final List<String> wallets = new ArrayList<>(List.of("CARD", "CASH"));

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {

        // 🔐 текущий пользователь
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // 📊 только его транзакции
        List<Transaction> transactions =
                transactionRepository.findByUserOrderByDateDescIdDesc(user);

        Double income = transactionRepository.getTotalIncome(user);
        Double expense = transactionRepository.getTotalExpense(user);

        if (income == null) income = 0.0;
        if (expense == null) expense = 0.0;

        double balance = income - expense;

        // 👉 баланс по кошелькам
        Map<String, Double> walletBalances = new LinkedHashMap<>();
        for (String w : wallets) {
            Double inc = transactionRepository.getTotalIncomeByWallet(user, w);
            Double exp = transactionRepository.getTotalExpenseByWallet(user, w);

            if (inc == null) inc = 0.0;
            if (exp == null) exp = 0.0;

            walletBalances.put(w, inc - exp);
        }

        model.addAttribute("transactions", transactions);
        model.addAttribute("balance", balance);
        model.addAttribute("income", income);
        model.addAttribute("expense", expense);
        model.addAttribute("walletBalances", walletBalances);

        model.addAttribute("incomeCategories", incomeCategories);
        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("wallets", wallets);

        return "index";
    }

    @PostMapping("/add-wallet")
    public String addWallet(@RequestParam String wallet) {

        String normalized = wallet.trim().toUpperCase();
        if (!normalized.isBlank() && !wallets.contains(normalized)) {
            wallets.add(normalized);
        }

        return "redirect:/";
    }

    @PostMapping("/add")
    public String add(@RequestParam Double amount,
                      @RequestParam String type,
                      @RequestParam(required = false) String description,
                      @RequestParam String wallet,
                      @RequestParam String category,
                      Authentication auth) {

        User user = userRepository.findByUsername(auth.getName()).orElseThrow();

        Transaction t = new Transaction();
        t.setAmount(amount);
        t.setType(type);
        t.setDescription(description);
        t.setCategory(category);
        t.setWallet(wallet);
        t.setDate(LocalDateTime.now());
        t.setUser(user);

        transactionRepository.save(t);

        return "redirect:/";
    }

}