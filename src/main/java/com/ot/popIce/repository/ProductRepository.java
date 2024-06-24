package com.ot.popIce.repository;

import com.ot.popIce.dto.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    public Optional<Product> findById(long id);

    public List<Product> findByNameContaining(String letter);

    public Product findByName(String name);
}