package com.entregable.cafecavosh.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ProductoResponse(
    Long idProducto,
    Integer idCategoria,
    String nombre,
    String descripcion,
    BigDecimal precio,
    Boolean nuevo,
    Boolean favorito,
    Map<String, BigDecimal> opciones
){
}
