package com.ot.popIce.dao;

import com.ot.popIce.dto.CashRegister;
import com.ot.popIce.dto.Expense;
import com.ot.popIce.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class ExpenseDao {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Expense findById(long id) {
        Optional<Expense> optional = expenseRepository.findById(id);
        return optional.orElse(null);
    }

    public Page<Expense> findAll(int offset, int pageSize, String field) {
        return expenseRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }

    public Page<Expense> findByCreatedDateTimeBetween(LocalDate startDate, LocalDate endDate, int offset, int pageSize, String field) {
        return expenseRepository.findByCreatedDateTimeBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59), PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }

    public Page<Expense> findByPaymentMode(String paymentMode, int offset, int pageSize, String field) {
        return expenseRepository.findByPaymentMode(paymentMode, PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }
}