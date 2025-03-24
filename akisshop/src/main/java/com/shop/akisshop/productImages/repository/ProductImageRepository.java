package com.shop.akisshop.productImages.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.akisshop.product.entity.Product;
import com.shop.akisshop.productImages.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>{

    Optional<ProductImage> findByProduct(Product product);
}
