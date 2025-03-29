package com.shop.akisshop.user.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shop.akisshop.user.entity.UserAuth;
import com.shop.akisshop.user.repository.UserRepository;

@Service
public class UserDetailsImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsImpl (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        var user = userRepository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("email not found"));
        return new UserAuth(user);
    }
}
