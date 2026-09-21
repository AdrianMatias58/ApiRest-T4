package com.entregable.cafecavosh.mapper;

import com.entregable.cafecavosh.dto.ProductoResponse;
import com.entregable.cafecavosh.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapperConfig.class)
public interface ProductoMapper {

    //Mappear prodcuto con categoria
    @Mapping(source ="categoria.idCategoria" , target = "idCategoria")
    ProductoResponse toProdcutoResponse(Producto producto);
    //Mapper de lista de productos
    List<ProductoResponse> listarProductos(List<Producto> lista);
}
