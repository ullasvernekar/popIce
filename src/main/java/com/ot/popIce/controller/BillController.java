package com.ot.popIce.controller;

import com.ot.popIce.dto.Bill;
import com.ot.popIce.dto.ResponseStructure;
import com.ot.popIce.services.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RequestMapping(value = "/bill")
@RestController
public class BillController {

    @Autowired
    public BillService billService;

    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<ResponseStructure<Bill>> findById(@PathVariable long id) {
        return billService.findById(id);
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<ResponseStructure<Page<Bill>>> findAll(@RequestParam(defaultValue = "0") int offset,
                                                                 @RequestParam(defaultValue = "5") int pageSize,
                                                                 @RequestParam(defaultValue = "id") String field) {
        return billService.findAll(offset, pageSize, field);
    }

    @GetMapping(value = "/findByDateBetween")
    public ResponseEntity<ResponseStructure<Page<Bill>>> findByCreatedDateTimeBetween(@RequestParam LocalDate startDate,
                                                                                      @RequestParam LocalDate endDate,
                                                                                      @RequestParam(defaultValue = "0") int offset,
                                                                                      @RequestParam(defaultValue = "5") int pageSize,
                                                                                      @RequestParam(defaultValue = "id") String field) {
        return billService.findByCreatedDateTimeBetween(startDate, endDate, offset, pageSize, field);
    }

    @GetMapping(value = "/mostSellingProduct")
    public ResponseEntity<ResponseStructure<Map<String, Integer>>> mostSellingProduct() {
        return billService.mostSoldProduct();
    }

    @GetMapping(value = "/mostSoldProductDateBetween")
    public ResponseEntity<ResponseStructure<Map<String, Integer>>> mostSoldProductDateBetween(@RequestParam LocalDate startDate,
                                                                                              @RequestParam LocalDate endDate,
                                                                                              @RequestParam(defaultValue = "0") int offset,
                                                                                              @RequestParam(defaultValue = "5") int pageSize,
                                                                                              @RequestParam(defaultValue = "id") String field) {
        return billService.mostSoldProductDateBetween(startDate, endDate, offset, pageSize, field);
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<ResponseStructure<Double>> totalRevenue() {
        return billService.totalRevenue();
    }

    @GetMapping("/revenue-between")
    public ResponseEntity<ResponseStructure<Double>> revenueBetweenDate(@RequestParam LocalDate startDate,
                                                                        @RequestParam LocalDate endDate,
                                                                        @RequestParam(defaultValue = "0") int offset,
                                                                        @RequestParam(defaultValue = "5") int pageSize,
                                                                        @RequestParam(defaultValue = "id") String field) {
        return billService.revenueBetweenDate(startDate, endDate, offset, pageSize, field);
    }

    @GetMapping("/revenue/by-date-and-payment-mode")
    public ResponseEntity<ResponseStructure<Double>> getRevenueByDateAndPaymentMode(@RequestParam LocalDate startDate,
                                                                                    @RequestParam LocalDate endDate,
                                                                                    @RequestParam String paymentMode) {
        return billService.revenueBetweenDateAndPaymentMode(startDate, endDate, paymentMode);
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseStructure<Bill>> saveBill(@RequestBody Bill bill) {
        return billService.save(bill);
    }

    @GetMapping("/generate-csv/betweenDates")
    public ResponseEntity<InputStreamResource> generateCsvDatesBetween(@RequestParam(value = "startDate") LocalDate startDate,
                                                                       @RequestParam(value = "endDate") LocalDate endDate) {

        return billService.generateCsvBetweenDates(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    @GetMapping("/generate-csv/all")
    public ResponseEntity<InputStreamResource> generateCsvForAllBills() {
        return billService.generateCsvForAllBills();
    }
}