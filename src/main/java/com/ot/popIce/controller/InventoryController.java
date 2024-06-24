package com.ot.popIce.controller;

import com.ot.popIce.dto.Inventory;
import com.ot.popIce.dto.ResponseStructure;
import com.ot.popIce.services.InventoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/inventory")
@RestController
public class InventoryController {

    @Autowired
    public InventoryService inventoryService;

    @PostMapping(value = "/save")
    public ResponseEntity<ResponseStructure<Inventory>> save(@RequestBody Inventory inventory) {
        return inventoryService.save(inventory);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<ResponseStructure<Inventory>> update(@RequestBody Inventory inventory) {
        return inventoryService.update(inventory);
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<ResponseStructure<Inventory>> delete(@RequestParam long id) {
        return inventoryService.delete(id);
    }

    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<ResponseStructure<Inventory>> findById(@PathVariable long id) {
        return inventoryService.findById(id);
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<ResponseStructure<Page<Inventory>>> findAll(@RequestParam(defaultValue = "0") int offset,
                                                                      @RequestParam(defaultValue = "5") int pageSize,
                                                                      @RequestParam(defaultValue = "id") String field) {
        return inventoryService.findAll(offset, pageSize, field);
    }

    @GetMapping(value = "/findByNameContaining")
    public ResponseEntity<ResponseStructure<List<Inventory>>> findByNameContaining(@RequestParam String letter) {
        return inventoryService.findByNameContaining(letter);
    }

    @GetMapping(value = "/findProductById")
    public ResponseEntity<ResponseStructure<Inventory>> findProductById(@RequestParam long productId) {
        return inventoryService.findByProductId(productId);
    }
}