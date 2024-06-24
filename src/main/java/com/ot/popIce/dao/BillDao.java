package com.ot.popIce.dao;

import com.ot.popIce.dto.Bill;
import com.ot.popIce.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class BillDao {

    @Autowired
    private BillRepository billRepository;

    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    public void delete(Bill bill) {
        billRepository.delete(bill);
    }

    public Bill findById(long id) {
        Optional<Bill> optional = billRepository.findById(id);
        return optional.orElse(null);
    }

    public Page<Bill> findAll(int offset, int pageSize, String field) {
        return billRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }

    public Page<Bill> findByCreatedDateTimeBetween(LocalDate startDate, LocalDate endDate, int offset, int pageSize, String field) {
        return billRepository.findByCreatedDateTimeBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59), PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }
}