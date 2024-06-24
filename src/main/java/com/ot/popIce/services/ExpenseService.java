package com.ot.popIce.services;

import com.opencsv.CSVWriter;
import com.ot.popIce.dao.CashRegisterDao;
import com.ot.popIce.dao.ExpenseDao;
import com.ot.popIce.dto.CashRegister;
import com.ot.popIce.dto.Expense;
import com.ot.popIce.dto.ResponseStructure;
import com.ot.popIce.repository.CashRegisterRepository;
import com.ot.popIce.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CashRegisterDao cashRegisterDao;

    @Autowired
    private CashRegisterRepository cashRegisterRepository;

    @Transactional
    public ResponseEntity<ResponseStructure<Expense>> save(Expense expense) {
        ResponseStructure<Expense> responseStructure = new ResponseStructure<>();

        if (!isValidPaymentMode(expense.getPaymentMode())) {
            responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
            responseStructure.setMessage("Invalid payment mode. Only 'cash' or 'upi' are accepted.");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
        }
        Expense savedExpense;
        try {
            savedExpense = expenseRepository.save(expense);
        } catch (Exception e) {
            responseStructure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseStructure.setMessage("Failed to save expense: " + e.getMessage());
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<CashRegister> cashRegisters = cashRegisterRepository.findByPaymentMode(expense.getPaymentMode());

        if (cashRegisters.isEmpty()) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Cash register not found for payment mode: " + expense.getPaymentMode());
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
        CashRegister cashRegister = cashRegisters.get(0);

        double newBalance = cashRegister.getAmount() - expense.getAmount();
        if (newBalance < 0) {
            responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
            responseStructure.setMessage("Insufficient funds in cash register for the specified payment mode.");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
        }

        cashRegister.setAmount(newBalance);
        try {
            cashRegisterRepository.save(cashRegister);
        } catch (Exception e) {
            responseStructure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseStructure.setMessage("Failed to update cash register: " + e.getMessage());
            responseStructure.setData(null);

            expenseRepository.delete(savedExpense);
            return new ResponseEntity<>(responseStructure, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Expense saved successfully");
        responseStructure.setData(savedExpense);
        return new ResponseEntity<>(responseStructure, HttpStatus.OK);
    }

    private boolean isValidPaymentMode(String paymentMode) {
        return paymentMode != null && (paymentMode.equalsIgnoreCase("cash") || paymentMode.equalsIgnoreCase("upi"));
    }

    public ResponseEntity<InputStreamResource> generateCsvBetweenDates(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);

            System.out.println("Start Date Time: " + startDateTime);
            System.out.println("End Date Time: " + endDateTime);

            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);

            String[] header = {"Expense ID", "Description", "Payment Mode", "Amount", "Created Date"};
            csvWriter.writeNext(header);

            List<Expense> expenses = (List<Expense>) expenseRepository.findByCreatedDateTimeBetween(startDateTime, endDateTime);
            System.out.println("Found Expenses: " + expenses.size());

            if (expenses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            for (Expense expense : expenses) {
                String[] data = {
                        String.valueOf(expense.getId()),
                        expense.getDescription(),
                        expense.getPaymentMode(),
                        String.valueOf(expense.getAmount()),
                        (expense.getCreatedDateTime() != null) ? expense.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : ""
                };
                csvWriter.writeNext(data);
            }
            csvWriter.close();
            String csvContent = writer.toString();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
            InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expenses_between_" +
                    startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                    "_and_" +
                    endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                    ".csv");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(csvContent.getBytes(StandardCharsets.UTF_8).length)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Page<InputStreamResource>> generateCsvForAllExpenses(int offset, int pageSize, String field) {
        try {
            List<Expense> allExpenses = expenseRepository.findAll();

            if (allExpenses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);

            String[] header = {"Expense ID", "Description", "Payment Mode", "Amount", "Created Date"};
            csvWriter.writeNext(header);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Expense expense : allExpenses) {
                String[] data = {
                        String.valueOf(expense.getId()),
                        expense.getDescription(),
                        expense.getPaymentMode(),
                        String.valueOf(expense.getAmount()),
                        (expense.getCreatedDateTime() != null) ? expense.getCreatedDateTime().format(formatter) : "",
                };
                csvWriter.writeNext(data);
            }
            csvWriter.close();
            String csvContent = writer.toString();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
            InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_expenses.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(csvContent.getBytes(StandardCharsets.UTF_8).length)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body((Page<InputStreamResource>) resource);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseStructure<Page<Expense>>> findByPaymentMode(String paymentMode, int offset, int pageSize, String field) {
        ResponseStructure<Page<Expense>> responseStructure = new ResponseStructure<>();
        Page<Expense> expense = expenseDao.findByPaymentMode(paymentMode, offset, pageSize, field);
        if (expense == null) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No data found ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("found by payment mode ");
            responseStructure.setData(expense);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Page<Expense>>> findByCreatedDateTimeBetween(LocalDate startDate, LocalDate endDate, int offset, int pageSize, String field) {
        ResponseStructure<Page<Expense>> responseStructure = new ResponseStructure<>();
        Page<Expense> expense = expenseDao.findByCreatedDateTimeBetween(startDate, endDate, offset, pageSize, field);

        if (expense.isEmpty()) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No expenses found between " + startDate + " and " + endDate);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Expenses found between " + startDate + " and " + endDate);
            responseStructure.setData(expense);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Page<Expense>>> findAll(int offset, int pageSize, String field) {
        ResponseStructure<Page<Expense>> responseStructure = new ResponseStructure<>();
        Page<Expense> expense = expenseDao.findAll(offset, pageSize, field);
        if (Objects.isNull(expense)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No data found");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("found all data");
            responseStructure.setData(expense);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Expense>> findById(long id) {
        ResponseStructure<Expense> responseStructure = new ResponseStructure<>();
        Expense expense = expenseDao.findById(id);
        if (Objects.isNull(expense)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No Expenses found by id");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Expense found by id");
            responseStructure.setData(expense);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }
}