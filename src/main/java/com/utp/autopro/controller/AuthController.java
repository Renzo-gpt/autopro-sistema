package com.utp.autopro.controller;

import com.utp.autopro.model.EstadoOT;
import com.utp.autopro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final RepuestoRepository repuestoRepository;
    private final CitaRepository citaRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalVehiculos", vehiculoRepository.count());
        model.addAttribute("otEnDiagnostico", ordenTrabajoRepository.countByEstado(EstadoOT.EN_DIAGNOSTICO));
        model.addAttribute("otEnProceso", ordenTrabajoRepository.countByEstado(EstadoOT.EN_PROCESO));
        model.addAttribute("otFinalizadas", ordenTrabajoRepository.countByEstado(EstadoOT.FINALIZADO));
        long stockBajo = repuestoRepository.findAll().stream().filter(r -> r.isStockBajo()).count();
        model.addAttribute("alertasStock", stockBajo);
        model.addAttribute("ultimasOrdenes", ordenTrabajoRepository.findAllByOrderByFechaIngresoDesc());
        model.addAttribute("citasRecientes", citaRepository.findAllByOrderByIdDesc());
        return "dashboard";
    }
}
