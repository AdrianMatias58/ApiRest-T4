package com.entregable.cafecavosh.repository;

import com.entregable.cafecavosh.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositori extends JpaRepository<Usuario,Long> {

    // Metodo para LOGIN y la carga de UserDetails en Spring Security
    Optional<Usuario> findByEmail(String email);

    // Metodo de verificación para REGISTER (evita duplicados)
    boolean existsByEmail(String email);
}
