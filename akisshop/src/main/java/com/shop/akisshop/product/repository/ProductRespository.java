package com.shop.akisshop.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.akisshop.product.entity.Product;

public interface ProductRespository extends JpaRepository<Product, Long>{

    boolean existsByName (String name);
}
