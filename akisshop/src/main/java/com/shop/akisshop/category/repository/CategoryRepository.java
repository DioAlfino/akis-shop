package com.shop.akisshop.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.akisshop.category.entity.ProductCategory;

public interface CategoryRepository extends JpaRepository<ProductCategory, Long>{
    boolean existsByName(String name);
}
