package com.entregable.cafecavosh.repository;

import com.entregable.cafecavosh.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepositori extends JpaRepository<Token,Long> {
    Optional<Token> findByRefreshToken(String refreshToken);
}
