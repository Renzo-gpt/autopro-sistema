package com.utp.autopro.repository;

import com.utp.autopro.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByPlacaIgnoreCase(String placa);
}
