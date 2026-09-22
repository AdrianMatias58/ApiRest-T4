package com.entregable.cafecavosh.mapper;

import com.entregable.cafecavosh.dto.LoginResponse;
import com.entregable.cafecavosh.dto.RegistroResponse;
import com.entregable.cafecavosh.entity.Token;
import com.entregable.cafecavosh.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UsuarioMapper {
    //Mapper de LoginResponse con token
    @Mapping(target = "token" , source ="token" )
    @Mapping(target = "refreshToken", source = "refresToken")
    LoginResponse toLoginResponse(Usuario usuario, String token,String refresToken);
    //Mapper de Registro con token
    @Mapping(target = "token", source = "token")
    @Mapping(target = "refreshToken", source = "refresToken")
    RegistroResponse toRegistroResponse(Usuario usuario, String token, String refresToken);
}
