package com.ot.popIce.services;

import com.opencsv.CSVWriter;
import com.ot.popIce.dao.*;
import com.ot.popIce.dto.*;
import com.ot.popIce.repository.BillRepository;
import com.ot.popIce.repository.CashRegisterRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillDao billDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private CashRegisterDao cashRegisterDao;

    @Autowired
    private CashRegisterRepository cashRegisterRepository;

    @Transactional
    public ResponseEntity<ResponseStructure<Bill>> save(Bill bill) {
        ResponseStructure<Bill> responseStructure = new ResponseStructure<>();
        try {
            if (!"cash".equalsIgnoreCase(bill.getPaymentMode()) && !"upi".equalsIgnoreCase(bill.getPaymentMode())) {
                responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
                responseStructure.setMessage("Invalid payment mode. Only 'cash' or 'UPI' are accepted.");
                return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
            }

            List<BillProduct> billProducts = bill.getBillProduct();
            double totalAmount = 0;
            List<BillProduct> billProductList = new ArrayList<>();
            boolean inventoryError = false;

            for (BillProduct billProduct : billProducts) {
                Product product = productDao.findById(billProduct.getProduct().getId());
                double amount = product.getPrice() * billProduct.getQuantity();
                billProduct.setTotalPrice(amount);
                totalAmount += amount;

                Inventory inventory = inventoryDao.findByProductId(product.getId());
                if (inventory.getQuantity() >= billProduct.getQuantity()) {
                    double inventoryQty = inventory.getQuantity() - billProduct.getQuantity();
                    inventory.setQuantity(inventoryQty);
                    inventoryDao.save(inventory);
                    billProductList.add(billProduct);
                } else {
                    inventoryError = true;
                }
            }
            if (!inventoryError) {
                bill.setBillProduct(billProductList);
                bill.setTotalPrice(totalAmount);
                bill = billDao.save(bill);

                List<CashRegister> cashRegisters = cashRegisterDao.findByPaymentMode(bill.getPaymentMode());
                CashRegister cashRegister;
                if (!cashRegisters.isEmpty()) {
                    cashRegister = cashRegisters.get(0);
                    double newAmount = cashRegister.getAmount() + totalAmount;
                    cashRegister.setAmount(newAmount);
                } else {
                    cashRegister = new CashRegister();
                    cashRegister.setAmount(totalAmount);
                    cashRegister.setPaymentMode(bill.getPaymentMode());
                    cashRegister.setUpdatedDateTime(LocalDateTime.now());
                }
                cashRegisterRepository.save(cashRegister);

                responseStructure.setStatus(HttpStatus.CREATED.value());
                responseStructure.setMessage("Bill Saved Successfully");
                responseStructure.setData(bill);
                return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
            } else {
                responseStructure.setStatus(HttpStatus.CONFLICT.value());
                responseStructure.setMessage("Insufficient quantity available in the inventory");
                return new ResponseEntity<>(responseStructure, HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            responseStructure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseStructure.setMessage("An error occurred while saving the bill: " + e.getMessage());
            return new ResponseEntity<>(responseStructure, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseStructure<Bill>> findById(long id) {
        ResponseStructure<Bill> responseStructure = new ResponseStructure<>();

        Bill bill = billDao.findById(id);
        if (Objects.isNull(bill)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Bill Does Not Exists To Be Found By ID ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("BIll Found By ID = " + id);
            responseStructure.setData(bill);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Page<Bill>>> findByCreatedDateTimeBetween(LocalDate startDate, LocalDate endDate, int offset, int pageSize, String field) {
        ResponseStructure<Page<Bill>> responseStructure = new ResponseStructure<>();

        Page<Bill> bills = billDao.findByCreatedDateTimeBetween(startDate, endDate, offset, pageSize, field);
        if (bills.isEmpty()) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No bills found between " + startDate + " and " + endDate);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Bills found between " + startDate + " and " + endDate);
            responseStructure.setData(bills);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Page<Bill>>> findAll(int offset, int pageSize, String field) {
        ResponseStructure<Page<Bill>> responseStructure = new ResponseStructure<>();

        Page<Bill> billsPage = billDao.findAll(offset, pageSize, field);
        if (!billsPage.isEmpty()) {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("All Bill found ");
            responseStructure.setData(billsPage);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No Bill Found ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Map<String, Integer>>> mostSoldProduct() {
        ResponseStructure<Map<String, Integer>> responseStructure = new ResponseStructure<>();

        List<Bill> bills = billRepository.findAll();
        Map<String, Double> productSalesMap = new HashMap<>();
        for (Bill bill : bills) {
            List<BillProduct> billProducts = bill.getBillProduct();
            for (BillProduct billProduct : billProducts) {
                Product product = billProduct.getProduct();
                productSalesMap.put(product.getName(), productSalesMap.getOrDefault(product.getName(), 0.0) + billProduct.getQuantity());
            }
        }
        double maxSales = 0;
        for (double salesQuantity : productSalesMap.values()) {
            if (salesQuantity > maxSales) {
                maxSales = salesQuantity;
            }
        }
        List<String> mostSoldProducts = new ArrayList<>();
        for (Map.Entry<String, Double> entry : productSalesMap.entrySet()) {
            String productName = entry.getKey();
            double salesQuantity = entry.getValue();

            if (salesQuantity == maxSales) {
                mostSoldProducts.add(productName);
            }
        }
        if (!mostSoldProducts.isEmpty()) {
            Map<String, Integer> mostSoldProductMap = new HashMap<>();
            for (String productName : mostSoldProducts) {
                mostSoldProductMap.put(productName, (int) maxSales);
            }
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Most sold products retrieved successfully ");
            responseStructure.setData(mostSoldProductMap);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No sales data available ");
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Map<String, Integer>>> mostSoldProductDateBetween(LocalDate startDate, LocalDate endDate, int offset, int pageSize, String field) {
        ResponseStructure<Map<String, Integer>> responseStructure = new ResponseStructure<>();

        Page<Bill> bills = billDao.findByCreatedDateTimeBetween(startDate, endDate, offset, pageSize, field);
        Map<String, Double> productSalesMap = new HashMap<>();
        for (Bill bill : bills) {
            List<BillProduct> billProducts = bill.getBillProduct();
            for (BillProduct billProduct : billProducts) {
                Product product = billProduct.getProduct();
                productSalesMap.put(product.getName(), productSalesMap.getOrDefault(product.getName(), 0.0) + billProduct.getQuantity());
            }
        }
        double maxSales = 0;
        for (double salesQuantity : productSalesMap.values()) {
            if (salesQuantity > maxSales) {
                maxSales = salesQuantity;
            }
        }
        List<String> mostSoldProducts = new ArrayList<>();
        for (Map.Entry<String, Double> entry : productSalesMap.entrySet()) {
            String productName = entry.getKey();
            double salesQuantity = entry.getValue();

            if (salesQuantity == maxSales) {
                mostSoldProducts.add(productName);
            }
        }
        if (!mostSoldProducts.isEmpty()) {
            Map<String, Integer> mostSoldProductMap = new HashMap<>();
            for (String productName : mostSoldProducts) {
                mostSoldProductMap.put(productName, (int) maxSales);
            }
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Most sold products retrieved successfully ");
            responseStructure.setData(mostSoldProductMap);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No sales data available");
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Double>> totalRevenue() {
        ResponseStructure<Double> responseStructure = new ResponseStructure<>();

        Optional<Double> totalRevenueOptional = billRepository.sumTotalPrice();
        if (totalRevenueOptional.isPresent()) {
            double totalRevenue = totalRevenueOptional.get();
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Total Revenue Of PopIce ");
            responseStructure.setData(totalRevenue);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No Bills Found to calculate Total Revenue Of PopIce ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ResponseStructure<Double>> revenueBetweenDate(LocalDate startDate, LocalDate endDate, int offset, int pageSize, String field) {
        ResponseStructure<Double> responseStructure = new ResponseStructure<>();

        Page<Bill> bills = billDao.findByCreatedDateTimeBetween(startDate, endDate, offset, pageSize, field);
        double totalRevenue = bills.stream().mapToDouble(Bill::getTotalPrice).sum();

        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Total Revenue Of PopIce ");
        responseStructure.setData(totalRevenue);
        return new ResponseEntity<>(responseStructure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Double>> revenueBetweenDateAndPaymentMode(LocalDate startDate, LocalDate endDate, String paymentMode) {
        ResponseStructure<Double> responseStructure = new ResponseStructure<>();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Bill> bills = billRepository.findByCreatedDateTimeBetweenAndPaymentMode(startDateTime, endDateTime, paymentMode);
        double totalRevenue = bills.stream().mapToDouble
                (Bill::getTotalPrice).sum();

        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Total Revenue Of PopIce between " + startDate + " and " + endDate + " for payment mode: " + paymentMode);
        responseStructure.setData(totalRevenue);
        return new ResponseEntity<>(responseStructure, HttpStatus.OK);
    }

    public ResponseEntity<ResponseStructure<Double>> revenueByPaymentMode(String paymentMode) {
        ResponseStructure<Double> responseStructure = new ResponseStructure<>();

        List<Bill> bills = billRepository.findByPaymentMode(paymentMode);
        double totalRevenue = bills.stream().mapToDouble(Bill::getTotalPrice).sum();

        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Total Revenue Of PopIce by Payment Mode: " + paymentMode);
        responseStructure.setData(totalRevenue);
        return new ResponseEntity<>(responseStructure, HttpStatus.OK);
    }

    public ResponseEntity<InputStreamResource> generateCsvForAllBills() {
        try {
            List<Bill> allBills = billRepository.findAll();
            if (allBills.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);

            String[] header = {"Bill ID", "Total Price", "Payment Mode", "Product ID", "Product Name", "Quantity", "Price Per Product"};
            csvWriter.writeNext(header);

            for (Bill bill : allBills) {
                List<BillProduct> billProducts = bill.getBillProduct();
                for (BillProduct billProduct : billProducts) {
                    String[] data = {
                            String.valueOf(bill.getId()),
                            String.valueOf(bill.getTotalPrice()),
                            bill.getPaymentMode(),
                            String.valueOf(billProduct.getProduct().getId()),
                            billProduct.getProduct().getName(),
                            String.valueOf(billProduct.getQuantity()),
                            String.valueOf(billProduct.getTotalPrice())
                    };
                    csvWriter.writeNext(data);
                }
            }
            csvWriter.close();
            String csvContent = writer.toString();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(csvContent.getBytes());
            InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_bills.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(csvContent.getBytes().length)
                    .contentType(MediaType.parseMediaType("application/csv"))
                    .body(resource);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<InputStreamResource> generateCsvBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Bill> bills = billRepository.findByCreatedDateTimeBetween(startDate, endDate);

            if (bills.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);

            String[] header = {"Bill ID", "Total Price", "Payment Mode", "Bill Date", "Product ID", "Product Name", "Quantity", "Price Per Product"};
            csvWriter.writeNext(header);

            for (Bill bill : bills) {
                List<BillProduct> billProducts = bill.getBillProduct();
                for (BillProduct billProduct : billProducts) {
                    String[] data = {
                            String.valueOf(bill.getId()),
                            String.valueOf(bill.getTotalPrice()),
                            bill.getPaymentMode(),
                            bill.getCreatedDateTime().toString(),
                            String.valueOf(billProduct.getProduct().getId()),
                            billProduct.getProduct().getName(),
                            String.valueOf(billProduct.getQuantity()),
                            String.valueOf(billProduct.getTotalPrice())
                    };
                    csvWriter.writeNext(data);
                }
            }
            csvWriter.close();
            String csvContent = writer.toString();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(csvContent.getBytes());
            InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bills_between_dates.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(csvContent.getBytes().length)
                    .contentType(MediaType.parseMediaType("application/csv"))
                    .body(resource);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}