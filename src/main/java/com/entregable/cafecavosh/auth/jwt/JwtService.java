package com.entregable.cafecavosh.auth.jwt;

import com.entregable.cafecavosh.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jswSecrteKey;

    @Value("${jwt.access-token.expiration-ms}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-token.expiration-ms}")
    private long refreshTokenExpiration;

    public String generarToken (final Usuario usuario)
    {
        return buildToken(usuario, jwtExpiration);
    }
    public  String generarRefresToken(final Usuario usuario)
    {
        return  buildToken(usuario, refreshTokenExpiration);
    }
    private String buildToken(final Usuario usuario, final long tiempoExpiracion){
        return Jwts
                .builder()
                .id(usuario.getIdUsuario().toString())
                .subject(usuario.getCorreo())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tiempoExpiracion))
                .signWith(getSecretKey())
                .compact();
    }
    public Date extraerFechaExpiracion(String token){
        Claims claims = extraerAllClaim(token);
        return  claims.getExpiration();
    }
    public Boolean tokenValido (String refreshToken){
        try {
            Claims claims = extraerAllClaim(refreshToken);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // Si el token fue manipulado, expiró al parsearlo o no es válido,
            // extraerAllClaim lanzará una excepción (ej: ExpiredJwtException, SignatureException)
            return false;
        }
    }
    public String extraerCorreo(String token){
        Claims claims = extraerAllClaim(token);
        return claims.getSubject();
    }
    private Claims extraerAllClaim(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey(){
     byte[] keyBites = Decoders.BASE64.decode(jswSecrteKey);
     return Keys.hmacShaKeyFor(keyBites);
    }
}
