package com.ot.popIce.services;

import com.opencsv.CSVWriter;
import com.ot.popIce.dao.CashRegisterDao;
import com.ot.popIce.dto.CashRegister;
import com.ot.popIce.dto.ResponseStructure;
import com.ot.popIce.repository.CashRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CashRegisterService {

    @Autowired
    private CashRegisterDao cashRegisterDao;

    @Autowired
    private CashRegisterRepository cashRegisterRepository;

    public ResponseEntity<ResponseStructure<CashRegister>> findById(long id) {
        ResponseStructure<CashRegister> responseStructure = new ResponseStructure<>();

        CashRegister cashRegister = cashRegisterDao.findById(id);
        if (Objects.isNull(cashRegister)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No Expenses found by id");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Expense found by id");
            responseStructure.setData(cashRegister);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Page<CashRegister>>> findAll(int offset, int pageSize, String field) {
        ResponseStructure<Page<CashRegister>> responseStructure = new ResponseStructure<>();

        Page<CashRegister> cashRegisters = cashRegisterDao.findAll(offset, pageSize, field);
        if (Objects.isNull(cashRegisters)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No data found");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("found all data");
            responseStructure.setData(cashRegisters);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Page<CashRegister>>> findByUpdatedDateTimeBetween(LocalDateTime startDatetime, LocalDateTime endDatetime, int offset, int pageSize, String field) {
        ResponseStructure<Page<CashRegister>> responseStructure = new ResponseStructure<>();

        Page<CashRegister> cashRegisters = cashRegisterDao.findByUpdateDatedTimeBetween(startDatetime, endDatetime, offset, pageSize, field);
        if (cashRegisters.isEmpty()) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No expenses found between " + startDatetime + " and " + endDatetime);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Expenses found between " + startDatetime + " and " + endDatetime);
            responseStructure.setData(cashRegisters);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<List<CashRegister>>> findByPaymentMode(String paymentMode) {
        ResponseStructure<List<CashRegister>> responseStructure = new ResponseStructure<>();

        if (!"cash".equalsIgnoreCase(paymentMode) && !"upi".equalsIgnoreCase(paymentMode)) {
            responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
            responseStructure.setMessage("Invalid payment mode. Only 'cash' or 'UPI' are accepted.");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
        }

        List<CashRegister> cashRegisters = cashRegisterDao.findByPaymentMode(paymentMode);
        if (cashRegisters.isEmpty()) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No data found for payment mode: " + paymentMode);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Found data for payment mode: " + paymentMode);
            responseStructure.setData(cashRegisters);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }
}