package com.shop.akisshop.user.dto;

import lombok.Data;

@Data
public class LoginResponseDto {

    private String message;
    private String token;
    private String role;
}
