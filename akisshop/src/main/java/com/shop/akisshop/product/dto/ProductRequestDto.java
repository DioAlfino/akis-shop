package com.shop.akisshop.product.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProductRequestDto {

    private String name;
    private BigDecimal price;
    private Long categoryId;
    private Integer quantity;
    private MultipartFile imagFile;
    private Long imageToDelete;

}
