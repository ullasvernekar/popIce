package com.ot.popIce.controller;

import com.ot.popIce.dto.Product;
import com.ot.popIce.dto.ResponseStructure;
import com.ot.popIce.services.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/Product")
public class ProductController {

    @Autowired
    public ProductService productService;

    @PostMapping(value = "/save")
    public ResponseEntity<ResponseStructure<Product>> save(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<ResponseStructure<Product>> update(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<ResponseStructure<Product>> delete(@RequestParam long id) {
        return productService.delete(id);
    }

    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<ResponseStructure<Product>> findById(@PathVariable long id) {
        return productService.findById(id);
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<ResponseStructure<Page<Product>>> findAll(@RequestParam(defaultValue = "0") int offset,
                                                                    @RequestParam(defaultValue = "5") int pageSize,
                                                                    @RequestParam(defaultValue = "id") String field) {
        return productService.findAll(offset, pageSize, field);
    }

    @GetMapping(value = "/findByNameContaining")
    public ResponseEntity<ResponseStructure<List<Product>>> findByNameContaning(@RequestParam String letter) {
        return productService.findByNameContaining(letter);
    }
}