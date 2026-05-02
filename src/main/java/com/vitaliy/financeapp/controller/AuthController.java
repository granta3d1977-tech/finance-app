package com.vitaliy.financeapp.controller;

import com.vitaliy.financeapp.entity.User;
import com.vitaliy.financeapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "login";
    }

    // открыть страницу регистрации
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // обработка формы
    @PostMapping("/register")
    public String register(@ModelAttribute User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // по умолчанию обычный пользователь
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }

        userRepository.save(user);

        return "redirect:/login";
    }
}