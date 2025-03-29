package com.shop.akisshop.user.service;

import org.springframework.security.core.Authentication;

import com.shop.akisshop.user.dto.LoginResponseDto;
import com.shop.akisshop.user.dto.UserRegisterDto;

public interface UserService {

    UserRegisterDto register (UserRegisterDto userRegisterDto);
    LoginResponseDto generateToken (Authentication authentication);
}
