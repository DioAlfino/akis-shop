package com.shop.akisshop.category.service.impl;

import org.springframework.stereotype.Service;

import com.shop.akisshop.category.entity.ProductCategory;
import com.shop.akisshop.category.repository.CategoryRepository;
import com.shop.akisshop.category.service.CategoryService;
import com.shop.akisshop.exceptions.DataNotFoundException;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;


    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductCategory findById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Category not found"));
    }

}
