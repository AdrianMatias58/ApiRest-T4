package com.entregable.cafecavosh.repository;

import com.entregable.cafecavosh.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositori extends JpaRepository<Usuario,Long> {

    // Metodo para LOGIN y la carga de UserDetails en Spring Security
    Optional<Usuario> findByCorreo(String email);
    //obtener corroe mediante codigo y correo
    Optional<Usuario> findByCorreoAndCodigoValidacion(String correo, String codigoValidacion);    // Metodo de verificación para REGISTER (evita duplicados)
    boolean existsByCorreo(String email);

}
