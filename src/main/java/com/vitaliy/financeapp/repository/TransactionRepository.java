package com.vitaliy.financeapp.repository;

import com.vitaliy.financeapp.entity.Transaction;
import com.vitaliy.financeapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 📌 список операций
    List<Transaction> findByUser(User user);

    List<Transaction> findByUserOrderByDateDescIdDesc(User user);

    List<Transaction> findByUserId(Long userId);


    // 📊 ОБЩИЕ СУММЫ
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user = :user AND t.type = 'INCOME'
    """)
    Double getTotalIncome(@Param("user") User user);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user = :user AND t.type = 'EXPENSE'
    """)
    Double getTotalExpense(@Param("user") User user);

    // 📊 СУММЫ ПО КОШЕЛЬКУ
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user = :user AND t.type = 'INCOME' AND t.wallet = :wallet
    """)
    Double getTotalIncomeByWallet(@Param("user") User user,
                                  @Param("wallet") String wallet);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user = :user AND t.type = 'EXPENSE' AND t.wallet = :wallet
    """)
    Double getTotalExpenseByWallet(@Param("user") User user,
                                   @Param("wallet") String wallet);

}