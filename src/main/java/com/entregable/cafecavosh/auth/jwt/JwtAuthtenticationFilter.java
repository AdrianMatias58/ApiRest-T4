package com.entregable.cafecavosh.auth.jwt;

import com.entregable.cafecavosh.entity.Usuario;
import com.entregable.cafecavosh.repository.UsuarioRepositori;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthtenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsuarioRepositori usuarioRepositori;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().startsWith("/api/v1/auth");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            if ( requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")){
                filterChain.doFilter(request,response);
                return;
            }
            String jwt = requestTokenHeader.split("Bearer ")[1];
            String correo = jwtService.extraerCorreo(jwt);
            if (correo!= null && SecurityContextHolder.getContext().getAuthentication() == null){
                Usuario usuario = usuarioRepositori.findByCorreo(correo).orElse(null);
                if (usuario!= null && jwtService.tokenValido(jwt)){
                    UserDetails userDetails = User.builder()
                            .username(usuario.getCorreo())
                            .password(usuario.getPassword())
                            .build();
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            null
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response,null,e);
        }
    }

}
