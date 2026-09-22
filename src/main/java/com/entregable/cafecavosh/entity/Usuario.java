package com.entregable.cafecavosh.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idUsuario")
@ToString(exclude = {"tokens", "pedidos"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Boolean validado;

    @Column(name = "codigo_verificacion")
    private String codigoValidacion;
    @Builder.Default
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Token> tokens = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();
}
