package com.grafana_loki.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grafana_loki.dto.ProductRequestDTO;
import com.grafana_loki.dto.ProductResponseDTO;
import com.grafana_loki.exception.DuplicateResourceException;
import com.grafana_loki.exception.InsufficientStockException;
import com.grafana_loki.exception.ResourceNotFoundException;
import com.grafana_loki.model.Product;
import com.grafana_loki.repository.ProductRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        log.info("Attempting to create product with SKU: {}", request.getSku());

        if (productRepository.existsBySku(request.getSku())) {
            log.error("Failed to create product. SKU {} already exists.", request.getSku());
            throw new DuplicateResourceException("Product with SKU " + request.getSku() + " already exists.");
        }

        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product successfully created with ID: {}", savedProduct.getId());
        
        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        log.info("Fetching product details for ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product fetch failed. ID {} not found.", id);
                    return new ResourceNotFoundException("Product not found with ID: " + id);
                });

        return mapToResponse(product);
    }

    @Transactional
    public ProductResponseDTO purchaseProduct(Long id, int quantity) {
        log.info("Processing purchase request for Product ID: {}, Quantity: {}", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Purchase failed. Product ID {} doesn't exist.", id);
                    return new ResourceNotFoundException("Product not found with ID: " + id);
                });

        if (product.getStock() < quantity) {
            log.error("Purchase failed for ID {}. Requested: {}, Available: {}", id, quantity, product.getStock());
            throw new InsufficientStockException("Insufficient stock. Available: " + product.getStock());
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        log.info("Purchase successful. Updated stock for Product ID {} is now: {}", id, product.getStock());

        return mapToResponse(product);
    }

    private ProductResponseDTO mapToResponse(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStock() > 0 ? "IN_STOCK" : "OUT_OF_STOCK")
                .build();
    }
}
