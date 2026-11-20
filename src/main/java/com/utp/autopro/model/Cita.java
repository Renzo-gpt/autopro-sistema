package com.utp.autopro.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "citas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_cliente", nullable = false, length = 120)
    private String nombreCliente;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 10)
    private String placa;

    @Column(name = "servicio_solicitado", nullable = false, length = 120)
    private String servicioSolicitado;

    @Column(name = "fecha_preferida", nullable = false, length = 30)
    private String fechaPreferida;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = "PENDIENTE";
}
