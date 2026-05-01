package com.vitaliy.financeapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String type; // INCOME / EXPENSE

    private String description;

    private String category;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}