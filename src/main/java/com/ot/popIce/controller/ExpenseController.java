package com.ot.popIce.controller;

import com.ot.popIce.dto.Expense;
import com.ot.popIce.dto.ResponseStructure;
import com.ot.popIce.services.ExpenseService;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("/expense")
@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping(value = "/save")
    public ResponseEntity<ResponseStructure<Expense>> save(@RequestBody Expense expense) {
        return expenseService.save(expense);
    }

    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<ResponseStructure<Expense>> findById(@PathVariable long id) {
        return expenseService.findById(id);
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<ResponseStructure<Page<Expense>>> findAll(@RequestParam(defaultValue = "0") int offset,
                                                                    @RequestParam(defaultValue = "5") int pageSize,
                                                                    @RequestParam(defaultValue = "id") String field) {
        return expenseService.findAll(offset, pageSize, field);
    }

    @GetMapping(value = "/findByDateBetween")
    public ResponseEntity<ResponseStructure<Page<Expense>>> findByDateBetween(@RequestParam LocalDate startDate,
                                                                              @RequestParam LocalDate endDate,
                                                                              @RequestParam(defaultValue = "0") int offset,
                                                                              @RequestParam(defaultValue = "5") int pageSize,
                                                                              @RequestParam(defaultValue = "id") String field) {
        return expenseService.findByCreatedDateTimeBetween(startDate, endDate, offset, pageSize, field);
    }

    @GetMapping("/findByPaymentMode")
    public ResponseEntity<ResponseStructure<Page<Expense>>> findByPaymentMode(@RequestParam String paymentMode,
                                                                              @RequestParam(defaultValue = "0") int offset,
                                                                              @RequestParam(defaultValue = "5") int pageSize,
                                                                              @RequestParam(defaultValue = "id") String field) {
        return expenseService.findByPaymentMode(paymentMode, offset, pageSize, field);
    }

    @GetMapping("/generate-csv/betweenDates")
    public ResponseEntity<InputStreamResource> generateCsvDatesBetween(@RequestParam(value = "startDate") LocalDate startDate,
                                                                       @RequestParam(value = "endDate") LocalDate endDate) {
        return expenseService.generateCsvBetweenDates(startDate, endDate);
    }

    @GetMapping("/generate-csv/all")
    public ResponseEntity<Page<InputStreamResource>> generateCsvForAll(int offset, int pageSize, String field) {
        return expenseService.generateCsvForAllExpenses(offset, pageSize, field);
    }
}