package com.entregable.cafecavosh.dto;

public record RegistroResponse(
        String correo,
        String token,
        String refreshToken
){
}
