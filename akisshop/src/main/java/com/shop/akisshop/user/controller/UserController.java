package com.shop.akisshop.user.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.akisshop.response.Response;
import com.shop.akisshop.user.dto.LoginResponseDto;
import com.shop.akisshop.user.dto.UserRegisterDto;
import com.shop.akisshop.user.service.UserService;

import jakarta.servlet.http.Cookie;


@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody UserRegisterDto userRegisterDto) {
        return Response.successResponse("Registration successfully", userService.register(userRegisterDto));
    }

     @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody UserRegisterDto userLogin) throws IllegalAccessException {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getName(), userLogin.getPassword()));

        var ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(authentication);

        LoginResponseDto data = userService.generateToken(authentication);
        data.setMessage("succussfully logged in");

        Cookie cookie = new Cookie("sid", data.getToken());
        HttpHeaders headers = new HttpHeaders();
        headers.add("set-cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly");
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(data);
    }

}
