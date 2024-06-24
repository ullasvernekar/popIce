package com.ot.popIce.dao;

import com.ot.popIce.dto.CashRegister;
import com.ot.popIce.repository.CashRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CashRegisterDao {

    @Autowired
    private CashRegisterRepository cashRegisterRepository;

    public CashRegister findById(long id) {
        Optional<CashRegister> optional = cashRegisterRepository.findById(id);
        return optional.orElse(null);
    }

    public Page<CashRegister> findAll(int offset, int pageSize, String field) {
        return cashRegisterRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }

    public Page<CashRegister> findByUpdateDatedTimeBetween(LocalDateTime startDatetime, LocalDateTime endDatetime, int offset, int pageSize, String field) {
        return cashRegisterRepository.findByUpdatedDateTimeBetween(startDatetime, endDatetime, PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }

    public List<CashRegister> findByPaymentMode(String paymentMode) {
        return cashRegisterRepository.findByPaymentMode(paymentMode);
    }
}