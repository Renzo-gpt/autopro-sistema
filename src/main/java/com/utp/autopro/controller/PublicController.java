package com.utp.autopro.controller;

import com.utp.autopro.model.*;
import com.utp.autopro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PublicController {

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping({"/", "/inicio"})
    public String landing() {
        return "index";
    }

    @GetMapping("/seguimiento")
    public String seguimiento(@RequestParam(value = "placa", required = false) String placa, Model model) {
        if (placa != null && !placa.isBlank()) {
            String placaLimpia = placa.trim().toUpperCase();
            model.addAttribute("placaBuscada", placaLimpia);
            vehiculoRepository.findByPlacaIgnoreCase(placaLimpia).ifPresent(v -> model.addAttribute("vehiculo", v));
            List<OrdenTrabajo> historial = ordenTrabajoRepository.findByVehiculoPlacaIgnoreCaseOrderByFechaIngresoDesc(placaLimpia);
            model.addAttribute("ordenes", historial);
        }
        return "seguimiento";
    }

    @PostMapping("/citas/solicitar")
    public String solicitarCita(@RequestParam String nombreCliente,
                                @RequestParam String telefono,
                                @RequestParam String placa,
                                @RequestParam String servicioSolicitado,
                                @RequestParam String fechaPreferida,
                                @RequestParam(required = false) String observaciones,
                                RedirectAttributes flash) {
        Cita cita = Cita.builder()
                .nombreCliente(nombreCliente)
                .telefono(telefono)
                .placa(placa.toUpperCase())
                .servicioSolicitado(servicioSolicitado)
                .fechaPreferida(fechaPreferida)
                .observaciones(observaciones)
                .estado("PENDIENTE")
                .build();
        citaRepository.save(cita);
        flash.addFlashAttribute("citaExito", "¡Cita agendada con éxito! Un asesor de AutoPro se comunicará contigo pronto.");
        return "redirect:/#cita";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombreCompleto,
                                   @RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam(defaultValue = "ASESOR") Rol rol,
                                   RedirectAttributes flash) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            flash.addFlashAttribute("error", "El nombre de usuario ya está registrado.");
            return "redirect:/registro";
        }
        Usuario nuevo = Usuario.builder()
                .nombreCompleto(nombreCompleto)
                .username(username.trim())
                .password(passwordEncoder.encode(password))
                .rol(rol)
                .activo(true)
                .build();
        usuarioRepository.save(nuevo);
        flash.addFlashAttribute("exito", "Cuenta creada correctamente. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}
