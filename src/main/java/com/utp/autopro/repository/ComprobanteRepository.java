package com.utp.autopro.repository;

import com.utp.autopro.model.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    List<Comprobante> findAllByOrderByFechaEmisionDesc();
}
