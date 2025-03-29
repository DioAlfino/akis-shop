package com.shop.akisshop.product.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductListReponseDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private CategoryDto categoryDto;
    private ImageDto imageDto;

    @Data
    public static class CategoryDto{
        private long id;
        private String name;
    }

    @Data
    public static class ImageDto {
        private Long id;
        private String imageUrl;
    }
}
