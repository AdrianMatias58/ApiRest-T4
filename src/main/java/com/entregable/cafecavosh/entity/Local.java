package com.entregable.cafecavosh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "local")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idLocal")
@ToString(exclude = "pedidos")
public class Local {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_local")
    private Integer idLocal;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Builder.Default
    @OneToMany(mappedBy = "local", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();
}
