package com.utp.autopro.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comprobantes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Comprobante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_comprobante", nullable = false, unique = true, length = 25)
    private String numeroComprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false)
    private TipoComprobante tipoComprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false, unique = true)
    private OrdenTrabajo ordenTrabajo;
}
