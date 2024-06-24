package com.ot.popIce.services;

import com.ot.popIce.dao.InventoryDao;
import com.ot.popIce.dao.ProductDao;
import com.ot.popIce.dto.Inventory;
import com.ot.popIce.dto.Product;
import com.ot.popIce.dto.ResponseStructure;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private ProductDao productDao;

    @Transactional
    public ResponseEntity<ResponseStructure<Inventory>> save(Inventory inventory) {
        ResponseStructure<Inventory> responseStructure = new ResponseStructure<>();

        Long productId = inventory.getProduct().getId();
        Product product = productDao.findById(productId);

        if (product == null) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Product with ID " + productId + " not found. Inventory cannot be saved.");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        }
        inventory.setProduct(product);
        inventory.setStockQuantity(inventory.getQuantity());
        Inventory savedInventory = inventoryDao.save(inventory);

        responseStructure.setStatus(HttpStatus.CREATED.value());
        responseStructure.setMessage("Inventory saved for the product: " + product.getName());
        responseStructure.setData(savedInventory);
        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<ResponseStructure<Inventory>> update(Inventory updatedInventory) {
        ResponseStructure<Inventory> responseStructure = new ResponseStructure<>();

        Inventory inventory = inventoryDao.findById(updatedInventory.getId());
        if (inventory == null) {
            return notFoundResponse("Inventory", updatedInventory.getId());
        }

        Product product = productDao.findById(updatedInventory.getProduct().getId());
        if (product == null) {
            return notFoundResponse("Product", updatedInventory.getProduct().getId());
        }
        inventory.setProduct(product);
        inventory.setQuantity(inventory.getQuantity() + updatedInventory.getQuantity());
        inventory.setStockQuantity(inventory.getStockQuantity() + updatedInventory.getQuantity());
        Inventory savedInventory = inventoryDao.save(inventory);

        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Inventory with ID " + savedInventory.getId() + " updated successfully");
        responseStructure.setData(savedInventory);
        return new ResponseEntity<>(responseStructure, HttpStatus.OK);
    }

    private ResponseEntity<ResponseStructure<Inventory>> notFoundResponse(String entityName, Long id) {
        ResponseStructure<Inventory> responseStructure = new ResponseStructure<>();
        responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
        responseStructure.setMessage(entityName + " with ID " + id + " not found");
        responseStructure.setData(null);
        return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<ResponseStructure<Inventory>> delete(long id) {
        ResponseStructure<Inventory> responseStructure = new ResponseStructure<>();

        Inventory inventory = inventoryDao.findById(id);
        if (inventory == null) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Inventory with ID " + id + " doesn't exist to be deleted.");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            inventoryDao.delete(inventory);
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Inventory with ID " + id + " deleted successfully.");
            responseStructure.setData(inventory);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Inventory>> findById(long id) {
        ResponseStructure<Inventory> responseStructure = new ResponseStructure<>();
        Inventory inventory = inventoryDao.findById(id);
        if (Objects.isNull(inventory)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Inventory Not Found By Id " + id);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Inventory Does Not Exist To Be Found By Id " + id);
            responseStructure.setData(inventory);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Inventory>> findByProductId(long productId) {
        ResponseStructure<Inventory> responseStructure = new ResponseStructure<>();
        Inventory inventory = inventoryDao.findByProductId(productId);

        if (Objects.isNull(inventory)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Product with ID " + productId + " not found in the inventory");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Product with ID " + productId + " found in the inventory");
            responseStructure.setData(inventory);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<List<Inventory>>> findByNameContaining(String letter) {
        ResponseStructure<List<Inventory>> responseStructure = new ResponseStructure<>();
        List<Inventory> inventory = inventoryDao.findByNameContaining(letter);
        if (inventory.isEmpty()) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No Inventory found with name containing letter '" + letter + "'");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Inventory found with name containing letter '" + letter + "'");
            responseStructure.setData(inventory);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Page<Inventory>>> findAll(int offset, int pageSize, String field) {
        ResponseStructure<Page<Inventory>> responseStructure = new ResponseStructure<>();
        Page<Inventory> inventory = inventoryDao.findAll(offset, pageSize, field);
        if (Objects.isNull(inventory)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No Inventory Exists To Be Found ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("All Existing Inventory Found ");
            responseStructure.setData(inventory);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }
}