package com.shop.akisshop.product.service.impl;
import com.shop.akisshop.productImages.entity.ProductImage;
import com.shop.akisshop.productImages.repository.ProductImageRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shop.akisshop.category.entity.ProductCategory;
import com.shop.akisshop.category.service.CategoryService;
import com.shop.akisshop.cloudinary.service.CloudinaryService;
import com.shop.akisshop.exceptions.DataNotFoundException;
import com.shop.akisshop.product.dto.ProductRequestDto;
import com.shop.akisshop.product.dto.ProductResponseDto;
import com.shop.akisshop.product.entity.Product;
import com.shop.akisshop.product.repository.ProductRespository;
import com.shop.akisshop.product.service.ProductService;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRespository productRespository;
    private final CategoryService categoryService;
    private final ProductImageRepository productImageRepository;
    private final CloudinaryService cloudinaryService;

    public ProductServiceImpl (ProductRespository productRespository, CategoryService categoryService, ProductImageRepository productImageRepository, CloudinaryService cloudinaryService) {
        this.productRespository = productRespository;
        this.categoryService = categoryService;
        this.productImageRepository = productImageRepository;
        this.cloudinaryService = cloudinaryService;
    }
@Override
@Transactional
public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
    log.info("Creating product: {}", productRequestDto.getName());

    if (productRespository.existsByName(productRequestDto.getName())) {
        throw new DataNotFoundException("Product name already exists");
    }

    ProductCategory category = categoryService.findById(productRequestDto.getCategoryId());
    Product product = new Product();
    product.setName(productRequestDto.getName());
    product.setPrice(productRequestDto.getPrice());
    product.setQuantity(productRequestDto.getQuantity());
    product.setCategory(category);

    Product savedProduct = productRespository.save(product);
    log.info("Product saved with ID: {}", savedProduct.getId());

    if (Objects.nonNull(productRequestDto.getImagFile())) {
        MultipartFile file = productRequestDto.getImagFile();
        
        long maxFileSize = 1 * 1024 * 1024;
        if (file.getSize() > maxFileSize) {
            throw new DataNotFoundException("File size exceeds the maximum limit of 1MB");
        }
        
        String contentType = file.getContentType();
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            throw new DataNotFoundException("Only JPG and PNG image types are allowed");
        }

        try {
            log.info("Uploading image...");
            String imageUrl = cloudinaryService.uploadImage(file);
            log.info("Image uploaded successfully: {}", imageUrl);

            ProductImage image = new ProductImage();
            image.setProduct(savedProduct);
            image.setImageUrl(imageUrl);
            productImageRepository.save(image);
            log.info("Image saved in database for product ID: {}", savedProduct.getId());

        } catch (Exception e) { 
            log.error("Error while uploading image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }

    return ProductResponseDto.mapToDto(savedProduct);
}

    @Override
    @Transactional
    public String updateProduct(Long id, ProductRequestDto productRequestDto) {
        Product product = productRespository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("product not found with id" + id));
        ProductCategory category = categoryService.findById(productRequestDto.getCategoryId());

        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setCategory(category);
        product.setQuantity(productRequestDto.getQuantity());

        Long imageToDelete = productRequestDto.getImageToDelete();
        if (imageToDelete != null) {
            ProductImage imageToDeleteObj = productImageRepository.findById(imageToDelete)
                .orElseThrow(() -> new DataNotFoundException("image not found with id " + id));
                cloudinaryService.deleteImage(imageToDeleteObj.getImageUrl());
                productImageRepository.delete(imageToDeleteObj);
        }

        if (productRequestDto.getImagFile() != null && !productRequestDto.getImagFile().isEmpty()) {
            MultipartFile file = productRequestDto.getImagFile();

            long maxFileSize = 1 * 1024 * 1024;
            if (file.getSize() > maxFileSize) {
                throw new DataNotFoundException("file size too big, max is 1 MB");
            }

            String contentType = file.getContentType();
            if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
                throw new DataNotFoundException("Only JPG and PNG image type are allowed");
            }
            
            try {
                log.info("Checking existing image...");
                Optional<ProductImage> existingImage = productImageRepository.findByProduct(product);

                if (existingImage.isPresent()) {
                    String oldImageUrl = existingImage.get().getImageUrl();

                    cloudinaryService.deleteImage(oldImageUrl);
                    productImageRepository.delete(existingImage.get());
                }
                log.info("Uploading new image...");
                String newImageUrl = cloudinaryService.uploadImage(file);
                log.info("new image uploaded: {}", newImageUrl);
                
                ProductImage newImage = new ProductImage();
                newImage.setProduct(product);
                newImage.setImageUrl(newImageUrl);
                productImageRepository.save(newImage);
                log.info("Image saved in database for product ID: {}", product.getId());
            } catch (Exception e) { 
                log.error("Error while uploading image: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload image to Cloudinary", e);
            }
        }
        productRespository.save(product);
        return "product updated succuessfully";
    }

}
