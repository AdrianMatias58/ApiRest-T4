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
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UsuarioRepositori usuarioRepositori;
    private final TokenRepositori tokenRepositori;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final JavaMailSender javaMailSender;

    @Value("${app.api-base-url}")
    private String baseUrlApi;
    @Value("${spring.mail.username}")
    private String remitenteCorreo;

    @Transactional
    public RegistroResponse registrar(RegistroRequest registroRequest) {
        // Validar si el correo ya existe
        if (usuarioRepositori.existsByCorreo(registroRequest.correo())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }
        // Hashear password
        var passHaseada = passwordEncoder.encode(registroRequest.password());
        //Numero de validacion
        var codigo_validacion = UUID.randomUUID().toString();
        // Guardar usuario
        var user = Usuario.builder()
                .nombre(registroRequest.nombre())
                .correo(registroRequest.correo())
                .codigoValidacion(codigo_validacion)
                .validado(false)
                .password(passHaseada)
                .build();
        var saveUser = usuarioRepositori.saveAndFlush(user);
        // Enviar correo de vlaidacion

        // Generar tokens
        var token = jwtService.generarToken(saveUser);
        var refresToken = jwtService.generarRefresToken(saveUser);
        // Guardar Token
        guardarTokenUsuario(saveUser, refresToken);
        try {
            enviarCorreoValidacion(saveUser.getCorreo(), saveUser.getNombre(), saveUser.getCodigoValidacion());
        } catch (Exception e) {
        }
        return usuarioMapper.toRegistroResponse(saveUser, token, refresToken);
    }

    @Transactional
    public LoginResponse login(LoginResquest loginResquest) {
        var usuario = usuarioRepositori.findByCorreo(loginResquest.correo())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        if (!passwordEncoder.matches(loginResquest.password(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        // Generar Tokens
        var token = jwtService.generarToken(usuario);
        var refreshToken = jwtService.generarRefresToken(usuario);
        // Registrar token en la base de datos
        guardarTokenUsuario(usuario, refreshToken);
        return usuarioMapper.toLoginResponse(usuario, token, refreshToken);
    }

    @Transactional
    public LoginResponse refrescarToken(String refreshToken) {
        // 1. Validar expiración y firma matemática del JWT
        if (!jwtService.tokenValido(refreshToken)) {
            throw new RuntimeException("Refresh token expirado o firma inválida");
        }
        // 2. Buscar en la BD y comprobar que no esté revocado
        var tokenDB = tokenRepositori.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token no registrado"));
        if (Boolean.TRUE.equals(tokenDB.getRevocado())) {
            throw new RuntimeException("El token ha sido revocado");
        }
        // 3. Generar el nuevo Access Token
        var usuario = tokenDB.getUsuario();
        var nuevoAccessToken = jwtService.generarToken(usuario);
        return usuarioMapper.toLoginResponse(usuario, nuevoAccessToken, refreshToken);
    }

    @Transactional
    public Boolean validarCuenta(String codigo, String correo) {
        // 1. Buscar usuario por ambos criterios
        Usuario usuario = usuarioRepositori.findByCorreoAndCodigoValidacion(correo, codigo)
                .orElseThrow(() -> new RuntimeException("El código de verificación es inválido o el usuario no existe"));

        // 2. Marcar como validado y limpiar el token
        usuario.setValidado(true);
        usuario.setCodigoValidacion(null);

        // 3. Guardar los cambios
        usuarioRepositori.save(usuario);
        return true;
    }

    private void guardarTokenUsuario(Usuario usuario, String jwttoken) {
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

    private void enviarCorreoValidacion(String correoUsuario, String nombreUsuario, String codigoVerificacion) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Usas la variable inyectada + un nombre legible para el remitente
            helper.setFrom(remitenteCorreo, "CafeCavosh App");

            String urlValidacion = baseUrlApi + "/verificar?correo=" + correoUsuario + "&codigo=" + codigoVerificacion;

            String htmlBody = """
                <div style="font-family: Arial, sans-serif; text-align: center; padding: 20px;">
                    <h2>¡Hola, %s!</h2>
                    <p>Gracias por registrarte en CafeCavosh. Por favor confirma tu cuenta haciendo clic en el siguiente botón:</p>
                    <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold; margin-top: 10px;">
                        Confirmar Cuenta
                    </a>
                </div>
                """.formatted(nombreUsuario, urlValidacion);

            helper.setTo(correoUsuario);
            helper.setSubject("Verificación de Cuenta - CafeCavosh");
            helper.setText(htmlBody, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el correo de verificación", e);
        }
    }
}