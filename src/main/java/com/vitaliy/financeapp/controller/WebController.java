package com.vitaliy.financeapp.controller;

import com.vitaliy.financeapp.entity.Transaction;
import com.vitaliy.financeapp.entity.User;
import com.vitaliy.financeapp.repository.TransactionRepository;
import com.vitaliy.financeapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private final List<String> incomeCategories = new ArrayList<>(
            List.of("SALARY", "BONUS", "FREELANCE")
    );

    private final List<String> expenseCategories = new ArrayList<>(
            List.of("FOOD", "TRANSPORT", "SHOPPING", "OTHER")
    );

    // в памяти (для простоты)
    private static final List<String> wallets = new ArrayList<>(
            List.of("CARD", "CASH")
    );

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "1") Long userId,
                       @RequestParam(required = false) String from,
                       @RequestParam(required = false) String to,
                       @RequestParam(required = false) String filter,
                       Model model) {

        User user = userRepository.findById(userId).orElseThrow();

        LocalDate today = LocalDate.now();
        LocalDate defaultFrom = today.withDayOfMonth(1);
        LocalDate defaultTo = today.withDayOfMonth(today.lengthOfMonth());

        if (filter == null) {
            from = defaultFrom.toString();
            to = defaultTo.toString();
        } else {
            from = parseDateOrDefault(from, defaultFrom).toString();
            to = parseDateOrDefault(to, defaultTo).toString();
        }

        LocalDate parsedFrom = LocalDate.parse(from);
        LocalDate parsedTo = LocalDate.parse(to);
        if (parsedFrom.isAfter(parsedTo)) {
            LocalDate temp = parsedFrom;
            parsedFrom = parsedTo;
            parsedTo = temp;
        }

        List<Transaction> transactions =
                transactionRepository.findByUserOrderByDateDescIdDesc(user);

        Double income = transactionRepository.getTotalIncome(user);
        Double expense = transactionRepository.getTotalExpense(user);
        double balance = income - expense;

        Map<String, Double> walletBalances = new LinkedHashMap<>();
        for (String w : wallets) {
            Double inc = transactionRepository.getTotalIncomeByWallet(user, w);
            Double exp = transactionRepository.getTotalExpenseByWallet(user, w);
            walletBalances.put(w, inc - exp);
        }

        model.addAttribute("transactions", transactions);
        model.addAttribute("balance", balance);
        model.addAttribute("income", income);
        model.addAttribute("expense", expense);
        model.addAttribute("walletBalances", walletBalances);

        model.addAttribute("from", from);
        model.addAttribute("to", to);

        model.addAttribute("userId", userId);
        model.addAttribute("incomeCategories", incomeCategories);
        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("wallets", wallets);

        return "index";
    }

    @PostMapping("/add-wallet")
    public String addWallet(@RequestParam String wallet,
                            @RequestParam Long userId) {

        String normalizedWallet = wallet.trim().toUpperCase();
        if (!normalizedWallet.isBlank() && !wallets.contains(normalizedWallet)) {
            wallets.add(normalizedWallet);
        }

        return "redirect:/?userId=" + userId;
    }

    @PostMapping("/add")
    public String add(@RequestParam Long userId,
                      @RequestParam Double amount,
                      @RequestParam String type,
                      @RequestParam(required = false) String description,
                      @RequestParam String wallet,
                      @RequestParam String category) {

        User user = userRepository.findById(userId).orElseThrow();

        Transaction t = new Transaction();
        t.setAmount(amount);
        t.setType(type);
        t.setDescription(description);
        t.setCategory(category);
        t.setWallet(wallet);
        t.setDate(LocalDateTime.now());
        t.setUser(user);

        transactionRepository.save(t);

        return "redirect:/?userId=" + userId;
    }

    private LocalDate parseDateOrDefault(String value, LocalDate defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            return defaultValue;
        }
    }
}
