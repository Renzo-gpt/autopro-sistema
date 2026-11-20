package com.utp.autopro.controller;

import com.utp.autopro.model.*;
import com.utp.autopro.repository.*;
import com.utp.autopro.service.InventarioService;
import com.utp.autopro.service.OrdenTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenTrabajoService;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;

    @GetMapping
    public String listar(Authentication auth, Model model) {
        Usuario actual = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("ordenes", ordenTrabajoService.listarOrdenesPorUsuario(actual));
        model.addAttribute("vehiculos", vehiculoRepository.findAll());
        model.addAttribute("mecanicos", usuarioRepository.findByRolAndActivoTrue(Rol.MECANICO));
        return "ordenes/lista";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam Long vehiculoId,
                        @RequestParam Long mecanicoId,
                        @RequestParam Integer kilometraje,
                        @RequestParam String nivelCombustible,
                        @RequestParam String fallaReportada,
                        Authentication auth,
                        RedirectAttributes flash) {
        Usuario asesor = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
        OrdenTrabajo ot = ordenTrabajoService.aperturarOrden(
                vehiculoId, mecanicoId, asesor, kilometraje, nivelCombustible, fallaReportada);
        flash.addFlashAttribute("exito", "Orden de Trabajo " + ot.getCodigoOt() + " aperturada con éxito.");
        return "redirect:/ordenes/" + ot.getId();
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("orden", ordenTrabajoService.obtenerPorId(id));
        model.addAttribute("repuestos", inventarioService.listarTodos());
        model.addAttribute("estados", EstadoOT.values());
        return "ordenes/detalle";
    }

    @PostMapping("/{id}/repuestos")
    public String agregarRepuesto(@PathVariable Long id,
                                  @RequestParam Long repuestoId,
                                  @RequestParam int cantidad,
                                  RedirectAttributes flash) {
        try {
            ordenTrabajoService.agregarRepuestoAOrden(id, repuestoId, cantidad);
            flash.addFlashAttribute("exito", "Repuesto agregado y descontado del inventario.");
        } catch (IllegalStateException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/ordenes/" + id;
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                             @RequestParam String diagnosticoTecnico,
                             @RequestParam BigDecimal costoManoObra,
                             @RequestParam EstadoOT estado,
                             RedirectAttributes flash) {
        try {
            ordenTrabajoService.actualizarDiagnosticoYEstado(id, diagnosticoTecnico, costoManoObra, estado);
            flash.addFlashAttribute("exito", "Diagnóstico y estado de la OT actualizados.");
        } catch (IllegalStateException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/ordenes/" + id;
    }
}
