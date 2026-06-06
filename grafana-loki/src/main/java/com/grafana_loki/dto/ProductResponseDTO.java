package com.grafana_loki.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String sku;
    private Double price;
    private Integer stock;
    private String status;
}