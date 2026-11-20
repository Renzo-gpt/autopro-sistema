package com.utp.autopro.service;

import com.utp.autopro.model.Repuesto;
import com.utp.autopro.repository.RepuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final RepuestoRepository repuestoRepository;

    public List<Repuesto> listarTodos() {
        return repuestoRepository.findAll();
    }

    public Repuesto obtenerPorId(Long id) {
        return repuestoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repuesto no encontrado con ID: " + id));
    }

    @Transactional
    public Repuesto guardar(Repuesto repuesto) {
        return repuestoRepository.save(repuesto);
    }

    @Transactional
    public void descontarStock(Long repuestoId, int cantidad) {
        Repuesto repuesto = obtenerPorId(repuestoId);
        if (repuesto.getStockActual() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para '" + repuesto.getNombre()
                    + "'. Disponible: " + repuesto.getStockActual() + ", solicitado: " + cantidad);
        }
        repuesto.setStockActual(repuesto.getStockActual() - cantidad);
        repuestoRepository.save(repuesto);
    }

    @Transactional
    public void reponerStock(Long repuestoId, int cantidad) {
        Repuesto repuesto = obtenerPorId(repuestoId);
        repuesto.setStockActual(repuesto.getStockActual() + cantidad);
        repuestoRepository.save(repuesto);
    }
}
