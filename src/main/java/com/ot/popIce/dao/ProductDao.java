package com.ot.popIce.dao;

import com.ot.popIce.dto.Product;
import com.ot.popIce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductDao {

    @Autowired
    private ProductRepository productRepository;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public Product findById(long id) {
        Optional<Product> optional = productRepository.findById(id);
        return optional.orElse(null);
    }

    public Page<Product> findAll(int offset, int pageSize, String field) {
        return productRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }

    public List<Product> findByNameContaining(String letter) {
        return productRepository.findByNameContaining(letter);
    }

    public Product findByName(String name) {
        return productRepository.findByName(name);
    }
}