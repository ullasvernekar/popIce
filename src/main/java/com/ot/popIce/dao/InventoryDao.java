package com.ot.popIce.dao;

import com.ot.popIce.dto.Inventory;
import com.ot.popIce.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InventoryDao {

    @Autowired
    private InventoryRepository inventoryRepository;

    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public void delete(Inventory inventory) {
        inventoryRepository.delete(inventory);
    }

    public Inventory findById(long id) {
        Optional<Inventory> optional = inventoryRepository.findById(id);
        return optional.orElse(null);
    }

    public Page<Inventory> findAll(int offset, int pageSize, String field) {
        return inventoryRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }

    public List<Inventory> findByNameContaining(String letter) {
        return inventoryRepository.findByNameContaining(letter);
    }

    public Inventory findByProductId(long productId) {
        Optional<Inventory> optional = inventoryRepository.findByProductId(productId);
        return optional.orElse(null);
    }
}