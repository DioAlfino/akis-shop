package com.shop.akisshop.product.service.impl;
import com.shop.akisshop.productImages.entity.ProductImage;
import com.shop.akisshop.productImages.repository.ProductImageRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shop.akisshop.category.entity.ProductCategory;
import com.shop.akisshop.category.service.CategoryService;
import com.shop.akisshop.cloudinary.service.CloudinaryService;
import com.shop.akisshop.exceptions.DataNotFoundException;
import com.shop.akisshop.product.dto.ProductListReponseDto;
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
    @Override
    public List<ProductListReponseDto> getAllProduct(Pageable pageable) {
        Page<Product> products = productRespository.findAll(pageable);

        List<ProductListReponseDto> productList = products.stream()
            .map(product -> {
                ProductListReponseDto dto = new ProductListReponseDto();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setPrice(product.getPrice());
                dto.setQuantity(product.getQuantity());

                if (product.getCategory() != null) {
                    ProductListReponseDto.CategoryDto categoryDto = new ProductListReponseDto.CategoryDto();
                    categoryDto.setId(product.getCategory().getId());
                    categoryDto.setName(product.getCategory().getName());
                    dto.setCategoryDto(categoryDto);
                } else {
                    dto.setCategoryDto(null);
                }

                if (product.getProductImage() != null) {
                    ProductListReponseDto.ImageDto imageDto = new ProductListReponseDto.ImageDto();
                    imageDto.setId(product.getProductImage().getId());
                    imageDto.setImageUrl(product.getProductImage().getImageUrl());
                    dto.setImageDto(imageDto);
                } else {
                    dto.setImageDto(null);
                }
                return dto;
            }).collect(Collectors.toList());
            return productList;
    }
    @Override
    public List<ProductListReponseDto> getProductByCategory(Long categoryId, Pageable pageable) {
        Page<Product> products = productRespository.findByCategoryId(categoryId, pageable);

        List<ProductListReponseDto> productList = products.stream()
            .map(product -> {
                ProductListReponseDto dto = new ProductListReponseDto();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setPrice(product.getPrice());
                dto.setQuantity(product.getQuantity());

                if (product.getCategory() != null) {
                    ProductListReponseDto.CategoryDto categoryDto = new ProductListReponseDto.CategoryDto();
                    categoryDto.setId(product.getCategory().getId());
                    categoryDto.setName(product.getCategory().getName());
                    dto.setCategoryDto(categoryDto);
                } else {
                    dto.setCategoryDto(null);
                }

                if (product.getProductImage() != null) {
                    ProductListReponseDto.ImageDto imageDto = new ProductListReponseDto.ImageDto();
                    imageDto.setId(product.getProductImage().getId());
                    imageDto.setImageUrl(product.getProductImage().getImageUrl());
                    dto.setImageDto(imageDto);
                } else {
                    dto.setImageDto(null);
                }
               return dto;
                
        }).collect(Collectors.toList());
        return productList;
    }
    @Override
    public List<ProductListReponseDto> getProductByName(String name, Pageable pageable) {
        Page<Product> products = productRespository.findByNameContainingIgnoreCase(name, pageable);

        List<ProductListReponseDto> productList = products.stream()
            .map(product -> {
                ProductListReponseDto dto = new ProductListReponseDto();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setPrice(product.getPrice());
                dto.setQuantity(product.getQuantity());

                if (product.getCategory() != null) {
                    ProductListReponseDto.CategoryDto categoryDto = new ProductListReponseDto.CategoryDto();
                    categoryDto.setId(product.getCategory().getId());
                    categoryDto.setName(product.getCategory().getName());
                    dto.setCategoryDto(categoryDto);
                } else {
                    dto.setCategoryDto(null);
                }

                if (product.getProductImage() != null) {
                    ProductListReponseDto.ImageDto imageDto = new ProductListReponseDto.ImageDto();
                    imageDto.setId(product.getProductImage().getId());
                    imageDto.setImageUrl(product.getProductImage().getImageUrl());
                    dto.setImageDto(imageDto);
                } else {
                    dto.setImageDto(null);
                }
                return dto;
            }).collect(Collectors.toList());
            return productList;
    }
    @Override
    public void deleteProduct(Long id) {
        productRespository.deleteById(id);
    }
}
