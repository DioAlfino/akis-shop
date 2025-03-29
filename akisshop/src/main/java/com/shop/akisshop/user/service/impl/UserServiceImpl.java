package com.shop.akisshop.user.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.shop.akisshop.user.dto.LoginResponseDto;
import com.shop.akisshop.user.dto.UserRegisterDto;
import com.shop.akisshop.user.entity.User;
import com.shop.akisshop.user.repository.UserRepository;
import com.shop.akisshop.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public UserServiceImpl (UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public UserRegisterDto register(UserRegisterDto userRegisterDto) {
        if (userRepository.existsByName(userRegisterDto.getName())) {
            throw new IllegalStateException("user name already exist");
        }

        User user = new User();
        user.setName(userRegisterDto.getName());

        String encodePassword = passwordEncoder.encode(userRegisterDto.getPassword());
        user.setPassword(encodePassword);

        userRepository.save(user);
        return userRegisterDto;
    }

     public LoginResponseDto generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority:: getAuthority)
            .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(1, ChronoUnit.HOURS))
            .subject(authentication.getName())
            .claim("scope", scope)
            .claim("userId", userRepository.findByName(authentication.getName()).get().getId())
            .build();

            LoginResponseDto data = new LoginResponseDto();
            data.setRole(scope);
            var token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            data.setToken(token);
            return data;

    }

}
