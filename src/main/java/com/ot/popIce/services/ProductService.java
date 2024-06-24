package com.ot.popIce.services;

import com.ot.popIce.dao.BillDao;
import com.ot.popIce.dao.InventoryDao;
import com.ot.popIce.dao.ProductDao;
import com.ot.popIce.dto.BillProduct;
import com.ot.popIce.dto.Product;
import com.ot.popIce.dto.ResponseStructure;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private BillDao billDao;

    @Transactional
    public ResponseEntity<ResponseStructure<Product>> save(Product product) {
        ResponseStructure<Product> responseStructure = new ResponseStructure<>();

        Product product1 = productDao.findByName(product.getName());
        if (product1 != null && product1.getName().equalsIgnoreCase(product.getName())) {
            responseStructure.setStatus(HttpStatus.CONFLICT.value());
            responseStructure.setMessage("Product Already Exists So Cannot Be Saved " + product.getName());
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.CONFLICT);
        } else {
            productDao.save(product);
            responseStructure.setStatus(HttpStatus.CREATED.value());
            responseStructure.setMessage("Product Saved Successfully " + product.getName());
            responseStructure.setData(product);
            return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
        }
    }

    @Transactional
    public ResponseEntity<ResponseStructure<Product>> delete(long id) {
        ResponseStructure<Product> responseStructure = new ResponseStructure<>();

        Product product = productDao.findById(id);
        if (product == null) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Product with ID " + id + " does not exist.");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
        try {
            productDao.delete(product);
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Product with ID " + id + " deleted successfully.");
            responseStructure.setData(product);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        } catch (DataAccessException e) {
            responseStructure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseStructure.setMessage("Failed to delete product with ID " + id);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResponseStructure<Product>> findById(long id) {
        ResponseStructure<Product> responseStructure = new ResponseStructure<>();

        BillProduct billProduct = new BillProduct();
        Product product = productDao.findById(id);
        if (Objects.isNull(product)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Product Not Present To Be Found By ID " + id);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Product Found By ID Successfully" + id);
            responseStructure.setData(product);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<List<Product>>> findByNameContaining(String letter) {
        ResponseStructure<List<Product>> responseStructure = new ResponseStructure<>();

        List<Product> products = productDao.findByNameContaining(letter);
        if (products.isEmpty()) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No Product found with name containing letter '" + letter + "'");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Product found with name containing letter '" + letter + "'");
            responseStructure.setData(products);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Page<Product>>> findAll(int offset, int pageSize, String field) {
        ResponseStructure<Page<Product>> responseStructure = new ResponseStructure<>();

        Page<Product> products = productDao.findAll(offset, pageSize, field);
        if (Objects.isNull(products)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No Products Found ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("All Products Found ");
            responseStructure.setData(products);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<ResponseStructure<Product>> updateProduct(Product product) {
        ResponseStructure<Product> responseStructure = new ResponseStructure<>();

        Product product1 = productDao.findById(product.getId());
        if (Objects.isNull(product1)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Product Not Found To Update " + product.getName());
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            productDao.save(product);
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Product Updated " + product.getName());
            responseStructure.setData(product);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }
}