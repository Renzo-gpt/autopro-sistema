package com.utp.autopro.config;

import com.utp.autopro.model.*;
import com.utp.autopro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final RepuestoRepository repuestoRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Usuario admin = null, asesor = null, mecanico = null;
        if (usuarioRepository.count() == 0) {
            admin = usuarioRepository.save(Usuario.builder()
                    .username("admin").password(passwordEncoder.encode("123456"))
                    .nombreCompleto("Carlos Mendoza (Admin)").rol(Rol.ADMIN).activo(true).build());
            asesor = usuarioRepository.save(Usuario.builder()
                    .username("asesor").password(passwordEncoder.encode("123456"))
                    .nombreCompleto("Mariana Paucar (Asesora)").rol(Rol.ASESOR).activo(true).build());
            mecanico = usuarioRepository.save(Usuario.builder()
                    .username("mecanico").password(passwordEncoder.encode("123456"))
                    .nombreCompleto("Víctor Quintan (Mecánico)").rol(Rol.MECANICO).activo(true).build());
        } else {
            asesor = usuarioRepository.findByUsername("asesor").orElse(null);
            mecanico = usuarioRepository.findByUsername("mecanico").orElse(null);
        }

        Vehiculo v1 = null, v2 = null;
        if (clienteRepository.count() == 0) {
            Cliente c1 = clienteRepository.save(Cliente.builder()
                    .documento("45896321").nombres("Jorge Luis Paredes")
                    .telefono("987654321").email("jparedes@gmail.com").direccion("Av. Arequipa 1240, Lima").build());
            v1 = vehiculoRepository.save(Vehiculo.builder()
                    .placa("ABC-123").marca("Toyota").modelo("Corolla").anio(2021).color("Plata").cliente(c1).build());

            Cliente c2 = clienteRepository.save(Cliente.builder()
                    .documento("20514789632").nombres("Transportes Rápido S.A.C.")
                    .telefono("912345678").email("flota@rapidosac.pe").direccion("Jr. Los Pinos 450, Los Olivos").build());
            v2 = vehiculoRepository.save(Vehiculo.builder()
                    .placa("XYZ-789").marca("Hyundai").modelo("Accent").anio(2022).color("Blanco").cliente(c2).build());
        }

        Repuesto r1 = null, r2 = null;
        if (repuestoRepository.count() == 0) {
            r1 = repuestoRepository.save(Repuesto.builder()
                    .codigo("REP-001").nombre("Filtro de Aceite Bosch").categoria("Filtros")
                    .precioUnitario(new BigDecimal("45.00")).stockActual(19).stockMinimo(5).build());
            r2 = repuestoRepository.save(Repuesto.builder()
                    .codigo("REP-002").nombre("Aceite Sintético 5W-30 (Galón)").categoria("Lubricantes")
                    .precioUnitario(new BigDecimal("140.00")).stockActual(14).stockMinimo(4).build());
            repuestoRepository.save(Repuesto.builder()
                    .codigo("REP-003").nombre("Pastillas de Freno Delanteras").categoria("Frenos")
                    .precioUnitario(new BigDecimal("120.00")).stockActual(4).stockMinimo(5).build());
            repuestoRepository.save(Repuesto.builder()
                    .codigo("REP-004").nombre("Bujía Iridium NGK").categoria("Encendido")
                    .precioUnitario(new BigDecimal("35.00")).stockActual(30).stockMinimo(8).build());
        }

        if (ordenTrabajoRepository.count() == 0 && v1 != null && v2 != null && mecanico != null && asesor != null) {
            OrdenTrabajo ot1 = OrdenTrabajo.builder()
                    .codigoOt("OT-2026-0001")
                    .fechaIngreso(LocalDateTime.now().minusHours(4))
                    .kilometraje(45200)
                    .nivelCombustible("1/2")
                    .fallaReportada("Mantenimiento preventivo 45,000 km y ruido leve al frenar.")
                    .diagnosticoTecnico("Cambio de aceite sintético y filtro completado. Pastillas al 60% de vida útil.")
                    .costoManoObra(new BigDecimal("80.00"))
                    .estado(EstadoOT.FINALIZADO)
                    .vehiculo(v1)
                    .mecanico(mecanico)
                    .asesor(asesor)
                    .build();

            ot1.getDetalles().add(DetalleOrden.builder()
                    .ordenTrabajo(ot1).repuesto(r1).cantidad(1)
                    .precioUnitario(r1.getPrecioUnitario()).subtotal(r1.getPrecioUnitario()).build());
            ot1.getDetalles().add(DetalleOrden.builder()
                    .ordenTrabajo(ot1).repuesto(r2).cantidad(1)
                    .precioUnitario(r2.getPrecioUnitario()).subtotal(r2.getPrecioUnitario()).build());
            ot1.recalcularTotales();
            ordenTrabajoRepository.save(ot1);

            OrdenTrabajo ot2 = OrdenTrabajo.builder()
                    .codigoOt("OT-2026-0002")
                    .fechaIngreso(LocalDateTime.now().minusHours(1))
                    .kilometraje(62800)
                    .nivelCombustible("3/4")
                    .fallaReportada("Pérdida de potencia en subida y testigo Check Engine encendido.")
                    .diagnosticoTecnico("Escaneo computarizado en curso; revisión de sistema de encendido e inyectores.")
                    .costoManoObra(new BigDecimal("60.00"))
                    .estado(EstadoOT.EN_PROCESO)
                    .vehiculo(v2)
                    .mecanico(mecanico)
                    .asesor(asesor)
                    .build();
            ot2.recalcularTotales();
            ordenTrabajoRepository.save(ot2);
        }
    }
}
