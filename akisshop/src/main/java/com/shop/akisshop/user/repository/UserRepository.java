package com.shop.akisshop.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.akisshop.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName (String name);
    Optional<User> findByName(String name);
}
