package com.entregable.cafecavosh.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistroRequest (

        @NotBlank
        String correo,
        @NotBlank
        String nombre,
        @NotBlank
        String password
){
}
