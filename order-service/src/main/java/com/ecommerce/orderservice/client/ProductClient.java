package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.dto.ProductDto;
import com.ecommerce.orderservice.dto.StockReductionItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/v1/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);

    @PostMapping("/api/v1/products/reduce-stock")
    void reduceStock(@RequestBody List<StockReductionItem> items);
}
