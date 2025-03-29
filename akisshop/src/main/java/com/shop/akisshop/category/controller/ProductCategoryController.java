package com.shop.akisshop.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.akisshop.category.entity.ProductCategory;
import com.shop.akisshop.category.service.CategoryService;
import com.shop.akisshop.exceptions.DataNotFoundException;
import com.shop.akisshop.response.Response;


@RestController
@RequestMapping("api/v1/category")
public class ProductCategoryController {

    private final CategoryService categoryService;

    public ProductCategoryController (CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping()
    public ResponseEntity<?> createCatgeory (ProductCategory productCategory) {
        ProductCategory createdCategory = categoryService.createCategory(productCategory);
        return Response.successResponse("product category created", createdCategory);
    }

    @GetMapping()
    public ResponseEntity<?> getAllCategory () {
        List<ProductCategory> categories = categoryService.getAllCategory();
        return Response.successResponse("fetch all categories", categories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory (@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);;
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
