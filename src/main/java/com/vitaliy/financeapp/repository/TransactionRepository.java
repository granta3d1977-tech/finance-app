package com.vitaliy.financeapp.repository;

import com.vitaliy.financeapp.entity.Transaction;
import com.vitaliy.financeapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = 'INCOME'")
    Double getTotalIncome(User user);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = 'EXPENSE'")
    Double getTotalExpense(User user);

}
