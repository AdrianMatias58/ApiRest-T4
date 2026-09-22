package com.entregable.cafecavosh.producto.service;

import com.entregable.cafecavosh.dto.ProductoResponse;
import com.entregable.cafecavosh.mapper.ProductoMapper;
import com.entregable.cafecavosh.repository.ProductoRepositori;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoMapper productoMapper;
    private final ProductoRepositori productoRepositori;
    public List<ProductoResponse> obtenerProducto() {
        //optener productos
        var producto = productoRepositori.findAll();
        //mappear a respuesta
        List<ProductoResponse> Listaproductos = productoMapper.listarProductos(producto);
        //retornarLista
        return Listaproductos;
    }
}
