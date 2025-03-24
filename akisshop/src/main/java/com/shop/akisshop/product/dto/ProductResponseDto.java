package com.shop.akisshop.product.dto;

import java.math.BigDecimal;

import com.shop.akisshop.product.entity.Product;

import lombok.Data;

@Data
public class ProductResponseDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private Long categoryId;
    private String imageUrl;
    private Integer quantity;

    public static ProductResponseDto mapToDto (Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategory().getId());
        dto.setQuantity(product.getQuantity());
        return dto;
    }
}
