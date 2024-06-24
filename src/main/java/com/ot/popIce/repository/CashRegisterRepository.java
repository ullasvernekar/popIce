package com.ot.popIce.repository;

import com.ot.popIce.dto.CashRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

    public List<CashRegister> findByPaymentMode(String paymentMode);

    public List<CashRegister> findByUpdatedDateTimeBetween(LocalDateTime startDatetime, LocalDateTime endDatetime);

    public Page<CashRegister> findByUpdatedDateTimeBetween(LocalDateTime startDatetime, LocalDateTime endDatetime, Pageable pageable);
}