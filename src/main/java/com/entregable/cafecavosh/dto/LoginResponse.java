package com.entregable.cafecavosh.dto;


import jakarta.validation.constraints.NotBlank;

public record LoginResponse (
        String correo,
        String token,
        String refreshToken
){
}
