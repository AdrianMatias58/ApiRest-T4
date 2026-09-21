package com.entregable.cafecavosh.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginResquest(
        @NotBlank
        String correo,
        @NotBlank
        String password
){
}
