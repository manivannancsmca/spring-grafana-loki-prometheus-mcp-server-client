package com.grafana_loki.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.grafana_loki.dto.ProductRequestDTO;
import com.grafana_loki.dto.ProductResponseDTO;
import com.grafana_loki.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String welcome() {
        log.info("welcome to product application");
        if(true) {
            throw new RuntimeException("This is an error message");
        }
        	
        return "welcome to product application";
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<ProductResponseDTO> purchaseProduct(
            @PathVariable Long id, 
            @RequestParam int quantity) {
        return ResponseEntity.ok(productService.purchaseProduct(id, quantity));
    }
}
