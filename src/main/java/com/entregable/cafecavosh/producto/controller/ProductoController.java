package com.entregable.cafecavosh.producto.controller;

import com.entregable.cafecavosh.dto.ProductoResponse;
import com.entregable.cafecavosh.producto.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/productos")
public class ProductoController {
    private final ProductoService productoService;
    @GetMapping("/obtener")
    public ResponseEntity<List<ProductoResponse>> listarProductos(){
        return  ResponseEntity.ok(productoService.obtenerProducto());
    }
}
