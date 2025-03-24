package com.shop.akisshop.product.service;

import com.shop.akisshop.product.dto.ProductRequestDto;
import com.shop.akisshop.product.dto.ProductResponseDto;

public interface ProductService {

    ProductResponseDto createProduct (ProductRequestDto ProductRequestDto);
    String updateProduct (Long id, ProductRequestDto productRequestDto);
}
