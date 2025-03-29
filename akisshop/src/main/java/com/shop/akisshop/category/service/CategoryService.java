package com.shop.akisshop.category.service;

import java.util.List;

import com.shop.akisshop.category.entity.ProductCategory;

public interface CategoryService {
    ProductCategory findById (Long id);
    ProductCategory createCategory(ProductCategory productCategory);
    ProductCategory updateCategory(Long id, ProductCategory productCategory);
    List<ProductCategory> getAllCategory();
    void deleteCategory(Long id);
}
