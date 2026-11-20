package com.utp.autopro.service;

import com.utp.autopro.model.*;
import com.utp.autopro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;

    public List<OrdenTrabajo> listarOrdenesPorUsuario(Usuario usuario) {
        if (usuario.getRol() == Rol.MECANICO) {
            return ordenTrabajoRepository.findByMecanicoOrderByFechaIngresoDesc(usuario);
        }
        return ordenTrabajoRepository.findAllByOrderByFechaIngresoDesc();
    }

    public OrdenTrabajo obtenerPorId(Long id) {
        return ordenTrabajoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de Trabajo no encontrada"));
    }

    @Transactional
    public OrdenTrabajo aperturarOrden(Long vehiculoId, Long mecanicoId, Usuario asesor,
                                       Integer kilometraje, String nivelCombustible, String fallaReportada) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
        Usuario mecanico = usuarioRepository.findById(mecanicoId)
                .orElseThrow(() -> new IllegalArgumentException("Mecánico no encontrado"));

        long correlativo = ordenTrabajoRepository.count() + 1;
        String codigoOt = String.format("OT-2026-%04d", correlativo);

        OrdenTrabajo ot = OrdenTrabajo.builder()
                .codigoOt(codigoOt)
                .fechaIngreso(LocalDateTime.now())
                .kilometraje(kilometraje)
                .nivelCombustible(nivelCombustible)
                .fallaReportada(fallaReportada)
                .estado(EstadoOT.EN_DIAGNOSTICO)
                .vehiculo(vehiculo)
                .mecanico(mecanico)
                .asesor(asesor)
                .build();

        return ordenTrabajoRepository.save(ot);
    }

    @Transactional
    public void agregarRepuestoAOrden(Long ordenId, Long repuestoId, int cantidad) {
        OrdenTrabajo ot = obtenerPorId(ordenId);
        if (ot.getEstado() == EstadoOT.FINALIZADO || ot.getEstado() == EstadoOT.PAGADO) {
            throw new IllegalStateException("No se pueden añadir repuestos a una OT finalizada o pagada.");
        }

        inventarioService.descontarStock(repuestoId, cantidad);
        Repuesto repuesto = inventarioService.obtenerPorId(repuestoId);
        BigDecimal subtotalLinea = repuesto.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));

        DetalleOrden detalle = DetalleOrden.builder()
                .ordenTrabajo(ot)
                .repuesto(repuesto)
                .cantidad(cantidad)
                .precioUnitario(repuesto.getPrecioUnitario())
                .subtotal(subtotalLinea)
                .build();

        ot.getDetalles().add(detalle);
        if (ot.getEstado() == EstadoOT.EN_DIAGNOSTICO) {
            ot.setEstado(EstadoOT.EN_PROCESO);
        }
        ot.recalcularTotales();
        ordenTrabajoRepository.save(ot);
    }

    @Transactional
    public void actualizarDiagnosticoYEstado(Long ordenId, String diagnosticoTecnico,
                                             BigDecimal costoManoObra, EstadoOT nuevoEstado) {
        OrdenTrabajo ot = obtenerPorId(ordenId);
        if (ot.getEstado() == EstadoOT.PAGADO) {
            throw new IllegalStateException("La orden ya fue pagada y no puede modificarse.");
        }
        ot.setDiagnosticoTecnico(diagnosticoTecnico);
        ot.setCostoManoObra(costoManoObra != null ? costoManoObra : BigDecimal.ZERO);
        ot.setEstado(nuevoEstado);
        ot.recalcularTotales();
        ordenTrabajoRepository.save(ot);
    }
}
