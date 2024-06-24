package com.ot.popIce.controller;

import com.ot.popIce.dto.CashRegister;
import com.ot.popIce.dto.ResponseStructure;
import com.ot.popIce.services.CashRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping(value = "/cashRegister")
@RestController
public class CashRegisterController {

    @Autowired
    private CashRegisterService cashRegisterService;

    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<ResponseStructure<CashRegister>> findById(@PathVariable long id) {
        return cashRegisterService.findById(id);
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<ResponseStructure<Page<CashRegister>>> findAll(@RequestParam(defaultValue = "0") int offset,
                                                                         @RequestParam(defaultValue = "5") int pageSize,
                                                                         @RequestParam(defaultValue = "id") String field) {
        return cashRegisterService.findAll(offset, pageSize, field);
    }

    @GetMapping(value = "/findByDateBetween")
    public ResponseEntity<ResponseStructure<Page<CashRegister>>> findByUpdatedDateTimeBetween(@RequestParam LocalDateTime startDatetime,
                                                                                              @RequestParam LocalDateTime endDatetime,
                                                                                              @RequestParam(defaultValue = "0") int offset,
                                                                                              @RequestParam(defaultValue = "5") int pageSize,
                                                                                              @RequestParam(defaultValue = "id") String field) {
        return cashRegisterService.findByUpdatedDateTimeBetween(startDatetime, endDatetime, offset, pageSize, field);
    }

    @GetMapping("/findByPaymentMode")
    public ResponseEntity<ResponseStructure<List<CashRegister>>> findByPaymentMode(@RequestParam String paymentMode) {
        return cashRegisterService.findByPaymentMode(paymentMode);
    }
}