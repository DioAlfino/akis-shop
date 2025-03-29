package com.shop.akisshop.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.akisshop.product.entity.Product;

public interface ProductRespository extends JpaRepository<Product, Long>{

    boolean existsByName (String name);
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
