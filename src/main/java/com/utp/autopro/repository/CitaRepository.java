package com.utp.autopro.repository;

import com.utp.autopro.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findAllByOrderByIdDesc();
}
