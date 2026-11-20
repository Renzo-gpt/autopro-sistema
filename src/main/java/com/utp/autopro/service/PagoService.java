package com.utp.autopro.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.utp.autopro.model.*;
import com.utp.autopro.repository.ComprobanteRepository;
import com.utp.autopro.repository.OrdenTrabajoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final ComprobanteRepository comprobanteRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;

    public List<Comprobante> listarComprobantes() {
        return comprobanteRepository.findAllByOrderByFechaEmisionDesc();
    }

    public Comprobante obtenerPorId(Long id) {
        return comprobanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comprobante no encontrado"));
    }

    @Transactional
    public Comprobante emitirComprobante(Long ordenId, TipoComprobante tipoComprobante, MetodoPago metodoPago) {
        OrdenTrabajo ot = ordenTrabajoRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden de Trabajo no encontrada"));

        if (ot.getEstado() != EstadoOT.FINALIZADO) {
            throw new IllegalStateException("Solo se pueden facturar órdenes en estado FINALIZADO.");
        }

        ot.recalcularTotales();
        long correlativo = comprobanteRepository.count() + 1;
        String serie = (tipoComprobante == TipoComprobante.FACTURA) ? "F001" : "B001";
        String numeroComprobante = String.format("%s-%06d", serie, correlativo);

        Comprobante comprobante = Comprobante.builder()
                .numeroComprobante(numeroComprobante)
                .tipoComprobante(tipoComprobante)
                .metodoPago(metodoPago)
                .fechaEmision(LocalDateTime.now())
                .subtotal(ot.getSubtotal())
                .igv(ot.getIgv())
                .total(ot.getTotal())
                .ordenTrabajo(ot)
                .build();

        ot.setEstado(EstadoOT.PAGADO);
        ordenTrabajoRepository.save(ot);
        return comprobanteRepository.save(comprobante);
    }

    public byte[] generarPdfComprobante(Long comprobanteId) {
        Comprobante comp = obtenerPorId(comprobanteId);
        OrdenTrabajo ot = comp.getOrdenTrabajo();
        Cliente cliente = ot.getVehiculo().getCliente();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font fontTitulo = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(19, 34, 56));
            Font fontSubtitulo = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font fontNormal = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font fontBlanco = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

            Paragraph header = new Paragraph("AutoPro S.A.C. - SISTEMA DE GESTIÓN MECÁNICA", fontTitulo);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph sub = new Paragraph("RUC: 20601234567 | Av. Universitaria 1801, Lima - Perú\n"
                    + comp.getTipoComprobante().name() + " ELECTRÓNICA N° " + comp.getNumeroComprobante(), fontSubtitulo);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(15);
            document.add(sub);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(15);
            infoTable.addCell(new Phrase("Cliente: " + cliente.getNombres(), fontNormal));
            infoTable.addCell(new Phrase("Fecha Emisión: " + comp.getFechaEmision().format(fmt), fontNormal));
            infoTable.addCell(new Phrase("DNI/RUC: " + cliente.getDocumento(), fontNormal));
            infoTable.addCell(new Phrase("Orden Trabajo: " + ot.getCodigoOt(), fontNormal));
            infoTable.addCell(new Phrase("Vehículo: " + ot.getVehiculo().getMarca() + " " + ot.getVehiculo().getModelo() + " (" + ot.getVehiculo().getPlaca() + ")", fontNormal));
            infoTable.addCell(new Phrase("Método de Pago: " + comp.getMetodoPago().name(), fontNormal));
            document.add(infoTable);

            PdfPTable tablaDetalle = new PdfPTable(4);
            tablaDetalle.setWidthPercentage(100);
            tablaDetalle.setWidths(new float[]{4f, 1.2f, 1.5f, 1.5f});

            String[] headers = {"Descripción del Servicio / Repuesto", "Cant.", "P. Unit (S/)", "Subtotal (S/)"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontBlanco));
                cell.setBackgroundColor(new Color(19, 34, 56));
                cell.setPadding(6);
                tablaDetalle.addCell(cell);
            }

            tablaDetalle.addCell(new Phrase("Servicio Técnico y Mano de Obra (" + ot.getCodigoOt() + ")", fontNormal));
            tablaDetalle.addCell(new Phrase("1", fontNormal));
            tablaDetalle.addCell(new Phrase(ot.getCostoManoObra().toString(), fontNormal));
            tablaDetalle.addCell(new Phrase(ot.getCostoManoObra().toString(), fontNormal));

            for (DetalleOrden det : ot.getDetalles()) {
                tablaDetalle.addCell(new Phrase(det.getRepuesto().getNombre(), fontNormal));
                tablaDetalle.addCell(new Phrase(det.getCantidad().toString(), fontNormal));
                tablaDetalle.addCell(new Phrase(det.getPrecioUnitario().toString(), fontNormal));
                tablaDetalle.addCell(new Phrase(det.getSubtotal().toString(), fontNormal));
            }
            document.add(tablaDetalle);

            Paragraph totales = new Paragraph(
                    "\nSubtotal: S/ " + comp.getSubtotal() +
                    "\nIGV (18%): S/ " + comp.getIgv() +
                    "\nTOTAL A PAGAR: S/ " + comp.getTotal(), fontSubtitulo);
            totales.setAlignment(Element.ALIGN_RIGHT);
            document.add(totales);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del comprobante", e);
        }
    }
}
