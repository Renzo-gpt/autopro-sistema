package com.utp.autopro.controller;

import com.utp.autopro.model.Repuesto;
import com.utp.autopro.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("repuestos", inventarioService.listarTodos());
        model.addAttribute("repuesto", new Repuesto());
        return "inventario/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("repuesto") Repuesto repuesto,
                          BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("repuestos", inventarioService.listarTodos());
            return "inventario/lista";
        }
        inventarioService.guardar(repuesto);
        flash.addFlashAttribute("exito", "Repuesto guardado en el catálogo.");
        return "redirect:/inventario";
    }

    @PostMapping("/reponer/{id}")
    public String reponerStock(@PathVariable Long id, @RequestParam("cantidad") int cantidad, RedirectAttributes flash) {
        if (cantidad > 0) {
            inventarioService.reponerStock(id, cantidad);
            flash.addFlashAttribute("exito", "Stock actualizado correctamente.");
        }
        return "redirect:/inventario";
    }
}
