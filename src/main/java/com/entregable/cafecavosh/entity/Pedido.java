package com.entregable.cafecavosh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idPedido")
@ToString(exclude = {"usuario", "local", "detalles"})
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "total_general", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalGeneral;

    @Column(nullable = false)
    private Integer item;

    @Builder.Default
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private List<DetallePedido> detalles = new ArrayList<>();
}
