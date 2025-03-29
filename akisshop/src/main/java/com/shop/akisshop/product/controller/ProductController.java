package com.shop.akisshop.product.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shop.akisshop.product.dto.ProductListReponseDto;
import com.shop.akisshop.product.dto.ProductRequestDto;
import com.shop.akisshop.product.dto.ProductResponseDto;
import com.shop.akisshop.product.service.ProductService;
import com.shop.akisshop.response.Response;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController (ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(
        @RequestParam("name") String name,
        @RequestParam("price") BigDecimal price,
        @RequestParam("categoryId") Long categoryId,
        @RequestParam("quantity") Integer quantity,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
        ) {
            ProductRequestDto requestDto = new ProductRequestDto();
            requestDto.setName(name);
            requestDto.setPrice(price);
            requestDto.setCategoryId(categoryId);
            requestDto.setQuantity(quantity);
            requestDto.setImagFile(imageFile);

            ProductResponseDto responseDto = productService.createProduct(requestDto);
            return Response.successResponse("product created", responseDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(
        @PathVariable("id") Long id,
        @RequestParam("name") String name,
        @RequestParam("price") BigDecimal price,
        @RequestParam("categoryId") Long categoryId,
        @RequestParam("quantity") Integer quantity,
        @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
        @RequestParam(value = "imageToDelete", required = false) Long imageToDelete
    ) {
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setName(name);
        requestDto.setPrice(price);
        requestDto.setCategoryId(categoryId);
        requestDto.setQuantity(quantity);
        requestDto.setImagFile(imageFile);
        requestDto.setImageToDelete(imageToDelete);

        String responseMessage = productService.updateProduct(id, requestDto);
        return Response.successResponse(responseMessage, null);
    }

    @GetMapping
    public ResponseEntity<?> getAllProduct (
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<ProductListReponseDto> products = productService.getAllProduct(pageable);
        return Response.successResponse("all products", products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductByCategory(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<ProductListReponseDto> products = productService.getProductByCategory(categoryId, pageable);
        return Response.successResponse("all product by category", products);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getProductByName (
        @RequestParam String name,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<ProductListReponseDto> products = productService.getProductByName(name, pageable);
        return Response.successResponse("producsts matching the search", products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById (@PathVariable Long id) {
        productService.deleteProduct(id);
        return Response.successResponse("product with id " + id + " deleted successfully", null);
    }
    
}
