package com.shop.akisshop.cloudinary.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shop.akisshop.cloudinary.service.CloudinaryService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService{

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl (Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Error uploadig image");
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            String publicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1).split("\\.")[0];

       Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
       log.info("Image deleted: {}", result);
       
        } catch (Exception e) {
            log.error("Failed to delete image: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting image from cloudinary");
        }

    }

}
