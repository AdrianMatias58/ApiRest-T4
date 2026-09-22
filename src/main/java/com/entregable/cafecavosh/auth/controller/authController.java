package com.entregable.cafecavosh.auth.controller;

import com.entregable.cafecavosh.auth.service.AuthService;
import com.entregable.cafecavosh.dto.LoginResponse;
import com.entregable.cafecavosh.dto.LoginResquest;
import com.entregable.cafecavosh.dto.RegistroRequest;
import com.entregable.cafecavosh.dto.RegistroResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class authController {

    private  final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody  LoginResquest loginResquest
    ){
        return ResponseEntity.ok(authService.login(loginResquest));
    }
    @PostMapping("/register")
    public  ResponseEntity<RegistroResponse> register(
            @Valid @RequestBody RegistroRequest registroRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(registroRequest));
    }
    @PostMapping("/refresh")
    public  ResponseEntity<LoginResponse> refresh(
            @RequestHeader("Authorization") String tokenHeader
    ){
        String refreshToken = tokenHeader.startsWith("Bearer ")
                ? tokenHeader.substring(7)
                : tokenHeader;
        return ResponseEntity.ok(authService.refrescarToken(refreshToken));
    }
    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Object>> verificar(@RequestParam String correo, @RequestParam String codigo) {
        boolean verificado = authService.validarCuenta(codigo,correo);

        if (verificado) {
            return ResponseEntity.ok(Map.of(
                    "exito", true,
                    "mensaje", "Cuenta verificada correctamente."
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "exito", false,
                    "mensaje", "El enlace de verificación es inválido o ya fue utilizado."
            ));
        }
    }
}
