package com.utp.autopro.controller;

import com.utp.autopro.model.Cliente;
import com.utp.autopro.model.Vehiculo;
import com.utp.autopro.repository.ClienteRepository;
import com.utp.autopro.repository.VehiculoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteVehiculoController {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("vehiculos", vehiculoRepository.findAll());
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("vehiculo", new Vehiculo());
        return "clientes/lista";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@Valid @ModelAttribute("cliente") Cliente cliente,
                                 BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("vehiculos", vehiculoRepository.findAll());
            model.addAttribute("vehiculo", new Vehiculo());
            return "clientes/lista";
        }
        clienteRepository.save(cliente);
        flash.addFlashAttribute("exito", "Cliente registrado correctamente.");
        return "redirect:/clientes";
    }

    @PostMapping("/vehiculos/guardar")
    public String guardarVehiculo(@Valid @ModelAttribute("vehiculo") Vehiculo vehiculo,
                                  BindingResult result,
                                  @RequestParam("clienteId") Long clienteId,
                                  Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("vehiculos", vehiculoRepository.findAll());
            model.addAttribute("cliente", new Cliente());
            return "clientes/lista";
        }
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente inválido"));
        vehiculo.setPlaca(vehiculo.getPlaca().toUpperCase());
        vehiculo.setCliente(cliente);
        vehiculoRepository.save(vehiculo);
        flash.addFlashAttribute("exito", "Vehículo asociado correctamente.");
        return "redirect:/clientes";
    }
}
