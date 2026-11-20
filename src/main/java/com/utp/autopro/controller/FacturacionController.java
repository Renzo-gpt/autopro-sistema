package com.utp.autopro.controller;

import com.utp.autopro.model.*;
import com.utp.autopro.repository.OrdenTrabajoRepository;
import com.utp.autopro.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/facturacion")
@RequiredArgsConstructor
public class FacturacionController {

    private final PagoService pagoService;
    private final OrdenTrabajoRepository ordenTrabajoRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("ordenesPorCobrar", ordenTrabajoRepository.findByEstadoOrderByFechaIngresoDesc(EstadoOT.FINALIZADO));
        model.addAttribute("comprobantes", pagoService.listarComprobantes());
        model.addAttribute("tiposComprobante", TipoComprobante.values());
        model.addAttribute("metodosPago", MetodoPago.values());
        return "facturacion/lista";
    }

    @PostMapping("/emitir")
    public String emitir(@RequestParam Long ordenId,
                         @RequestParam TipoComprobante tipoComprobante,
                         @RequestParam MetodoPago metodoPago,
                         RedirectAttributes flash) {
        try {
            Comprobante comp = pagoService.emitirComprobante(ordenId, tipoComprobante, metodoPago);
            flash.addFlashAttribute("exito", "Comprobante " + comp.getNumeroComprobante() + " generado correctamente.");
        } catch (IllegalStateException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/facturacion";
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Comprobante comp = pagoService.obtenerPorId(id);
        byte[] pdfBytes = pagoService.generarPdfComprobante(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(comp.getNumeroComprobante() + ".pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
