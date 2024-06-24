package com.ot.popIce.repository;

import com.ot.popIce.dto.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    public List<Inventory> findByNameContaining(String letter);

    public Optional<Inventory> findByProductId(long id);
}