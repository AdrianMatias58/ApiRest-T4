package com.entregable.cafecavosh.auth.service;

import com.entregable.cafecavosh.auth.jwt.JwtService;
import com.entregable.cafecavosh.dto.LoginResponse;
import com.entregable.cafecavosh.dto.LoginResquest;
import com.entregable.cafecavosh.dto.RegistroRequest;
import com.entregable.cafecavosh.dto.RegistroResponse;
import com.entregable.cafecavosh.entity.Token;
import com.entregable.cafecavosh.entity.Usuario;
import com.entregable.cafecavosh.mapper.UsuarioMapper;
import com.entregable.cafecavosh.repository.TokenRepositori;
import com.entregable.cafecavosh.repository.UsuarioRepositori;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UsuarioRepositori usuarioRepositori;
    private final TokenRepositori tokenRepositori;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public RegistroResponse registrar(RegistroRequest registroRequest){
        //hasear passwor
        var passHaseada = passwordEncoder.encode(registroRequest.password());

        //guardar usuario
        var user = Usuario.builder()
                .nombre(registroRequest.nombre())
                .correo(registroRequest.correo())
                .password(passHaseada)
                .build();
        var saveUser = usuarioRepositori.save(user);
        //generar tookens
        var token = jwtService.generarToken(saveUser);
        var refresToken = jwtService.generarRefresToken(saveUser);
        //guardar Token
        guardarTokenUsuario(saveUser,refresToken);
        return usuarioMapper.toRegistroResponse(saveUser, token, refresToken);
    }

    @Transactional
    public LoginResponse login(LoginResquest loginResquest){

    }

    @Transactional
    private void guardarTokenUsuario(Usuario usuario, String jwttoken){
        var token = Token.builder()
                .usuario(usuario)
                .refreshToken(jwttoken)
                .userAgent("app_android")
                .ipOrigen("122133555")
                .expiraEn(jwtService.extraerFechaExpiracion(jwttoken))
                .revocado(false)
                .creadoEn(new Date())
                .build();
        tokenRepositori.save(token);
    }
}
