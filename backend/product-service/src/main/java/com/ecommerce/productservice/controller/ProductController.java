package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.StockReductionItem;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(required = false) String category) {
        if (category != null && !category.trim().isEmpty()) {
            return ResponseEntity.ok(productRepository.findByCategory(category));
        }
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with ID: " + id);
        }
        return ResponseEntity.ok(productOpt.get());
    }

    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestBody Product product,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        
        // Secure ADMIN action using headers propagated by API Gateway
        if (userRole == null || !userRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators can create products");
        }

        Product savedProduct = productRepository.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @PostMapping("/reduce-stock")
    @Transactional
    public ResponseEntity<?> reduceStock(@RequestBody List<StockReductionItem> items) {
        for (StockReductionItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + item.getProductId()));
            
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName() 
                        + " (Requested: " + item.getQuantity() + ", Available: " + product.getStockQuantity() + ")");
            }
            
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }
        return ResponseEntity.ok("Stock updated successfully");
    }
}
