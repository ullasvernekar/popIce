package com.ot.popIce.repository;

import com.ot.popIce.dto.CashRegister;
import com.ot.popIce.dto.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    public Expense findByPaymentMode(String paymentMode);

    public Page<Expense> findByPaymentMode(String paymentMode, Pageable pageable);

    public List<Expense> findByCreatedDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    public Page<Expense> findByCreatedDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest);
}