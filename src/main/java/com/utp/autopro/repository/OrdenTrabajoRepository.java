package com.utp.autopro.repository;

import com.utp.autopro.model.EstadoOT;
import com.utp.autopro.model.OrdenTrabajo;
import com.utp.autopro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {
    List<OrdenTrabajo> findAllByOrderByFechaIngresoDesc();
    List<OrdenTrabajo> findByMecanicoOrderByFechaIngresoDesc(Usuario mecanico);
    List<OrdenTrabajo> findByEstadoOrderByFechaIngresoDesc(EstadoOT estado);
    List<OrdenTrabajo> findByVehiculoPlacaIgnoreCaseOrderByFechaIngresoDesc(String placa);
    long countByEstado(EstadoOT estado);
}
