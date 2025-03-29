package com.shop.akisshop.product.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.shop.akisshop.product.dto.ProductListReponseDto;
import com.shop.akisshop.product.dto.ProductRequestDto;
import com.shop.akisshop.product.dto.ProductResponseDto;

public interface ProductService {

    ProductResponseDto createProduct (ProductRequestDto ProductRequestDto);
    String updateProduct (Long id, ProductRequestDto productRequestDto);
    List<ProductListReponseDto> getAllProduct(Pageable pageable);
    List<ProductListReponseDto> getProductByCategory(Long categoryId, Pageable pageable);
    List<ProductListReponseDto> getProductByName (String name, Pageable pageable);
    void deleteProduct (Long id);
}
