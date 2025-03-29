package com.shop.akisshop.category.service.impl;

import java.util.List;
import java.util.Optional;

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

    @Override
    public ProductCategory createCategory(ProductCategory productCategory) {
        if (categoryRepository.existsByName(productCategory.getName())) {
            throw new DataNotFoundException("Product category name already exist");
        }
        return categoryRepository.save(productCategory);
    }

    @Override
    public ProductCategory updateCategory(Long id, ProductCategory productCategory) {
       Optional<ProductCategory> existingProduct = categoryRepository.findById(id);
        if (existingProduct.isPresent()) {
            ProductCategory categoryUpdate = existingProduct.get();
            categoryUpdate.setName(productCategory.getName());
            return categoryRepository.save(categoryUpdate);
        } else {
            throw new DataNotFoundException("Product withid not found with id " + id);
        }
    }

    @Override
    public List<ProductCategory> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new DataNotFoundException("category with id " + id + " not found");
        }
    }

    

}
