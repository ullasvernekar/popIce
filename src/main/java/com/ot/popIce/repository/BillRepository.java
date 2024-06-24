package com.ot.popIce.repository;

import com.ot.popIce.dto.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    public Page<Bill> findByCreatedDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest);

    public List<Bill> findByCreatedDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    public List<Bill> findByCreatedDateTimeBetweenAndPaymentMode(LocalDateTime startDateTime, LocalDateTime endDateTime, String paymentMode);

    public List<Bill> findByPaymentMode(String paymentMode);

    @Query("SELECT SUM(b.totalPrice) FROM Bill b")
    public Optional<Double> sumTotalPrice();

    public Page<Bill> findAll(Pageable pageable);
}