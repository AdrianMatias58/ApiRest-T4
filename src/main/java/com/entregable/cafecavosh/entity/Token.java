package com.entregable.cafecavosh.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idToken")
@ToString(exclude = "usuario")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Long idToken;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "refresh_token", nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "expira_en", nullable = false)
    private Date expiraEn;

    @Column(nullable = false)
    private Boolean revocado;

    @Column(name = "creado_en",insertable = false, nullable = false)
    private Date creadoEn;
}
