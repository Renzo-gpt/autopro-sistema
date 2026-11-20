package com.utp.autopro.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes_trabajo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_ot", nullable = false, unique = true, length = 20)
    private String codigoOt;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(nullable = false)
    private Integer kilometraje;

    @Column(name = "nivel_combustible", nullable = false, length = 20)
    private String nivelCombustible;

    @Column(name = "falla_reportada", nullable = false, columnDefinition = "TEXT")
    private String fallaReportada;

    @Column(name = "diagnostico_tecnico", columnDefinition = "TEXT")
    private String diagnosticoTecnico;

    @Column(name = "costo_mano_obra", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoManoObra = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal igv = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoOT estado = EstadoOT.EN_DIAGNOSTICO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mecanico_id", nullable = false)
    private Usuario mecanico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asesor_id", nullable = false)
    private Usuario asesor;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleOrden> detalles = new ArrayList<>();

    public void recalcularTotales() {
        BigDecimal totalRepuestos = detalles.stream()
                .map(DetalleOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal manoObra = (costoManoObra != null) ? costoManoObra : BigDecimal.ZERO;
        this.subtotal = totalRepuestos.add(manoObra).setScale(2, RoundingMode.HALF_UP);
        this.igv = this.subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        this.total = this.subtotal.add(this.igv).setScale(2, RoundingMode.HALF_UP);
    }
}
