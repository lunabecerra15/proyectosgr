package com.zapacciano.sgr.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.zapacciano.sgr.model.ItemPedido;
import com.zapacciano.sgr.model.Pedido;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
//import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    /**
     * Genera un reporte en PDF basado en una lista real de pedidos completados (ventas).
     * ¡Esta versión usa OpenPDF!
     * @param ventas Lista de pedidos con estado COMPLETADO.
     * @return un array de bytes (byte[]) que representa el archivo PDF.
     */
    public byte[] generarReporteVentas(List<Pedido> ventas) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4); // Tamaño A4

        try {
            PdfWriter.getInstance(document, baos); // Escribimos en memoria
            document.open();

            // Estilos de Fuente
            Font fontTitulo = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font fontFecha = new Font(Font.HELVETICA, 12, Font.ITALIC);
            Font fontHeader = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
            Font fontTotal = new Font(Font.HELVETICA, 14, Font.BOLD);

            // --- 1. TÍTULO ---
            Paragraph titulo = new Paragraph("Reporte de Ventas - Zapacciano", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            
            Paragraph fecha = new Paragraph("Generado el: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontFecha);
            fecha.setAlignment(Element.ALIGN_CENTER);
            fecha.setSpacingAfter(20); // Espacio después de la fecha
            document.add(fecha);

            // --- 2. TABLA DE VENTAS ---
            PdfPTable table = new PdfPTable(4); // 4 columnas
            table.setWidthPercentage(100); // Ancho 100%
            table.setWidths(new float[]{1f, 3f, 4f, 2f}); // Anchos de columna

            // Encabezados de la tabla
            table.addCell(crearCeldaHeader("ID Pedido", fontHeader));
            table.addCell(crearCeldaHeader("Mesa / Mozo", fontHeader));
            table.addCell(crearCeldaHeader("Items Vendidos", fontHeader));
            table.addCell(crearCeldaHeader("Total", fontHeader));
            
            double granTotal = 0.0;

            // --- 3. BUCLE DE DATOS REALES ---
            for (Pedido venta : ventas) {
                
                table.addCell(String.valueOf(venta.getId())); // Columna 1: ID
                
                // Columna 2: Mesa y Mozo
                String infoMesaMozo = String.format("Mesa N°: %d\nMozo: %s", 
                                                    venta.getMesa().getNumero(), 
                                                    venta.getUsuario().getNombre());
                table.addCell(infoMesaMozo);
                
                // Columna 3: Items
                StringBuilder itemsStr = new StringBuilder();
                for (ItemPedido item : venta.getItems()) {
                    itemsStr.append(String.format("%dx %s\n", 
                                                  item.getCantidad(), 
                                                  item.getProducto().getNombre()));
                }
                table.addCell(itemsStr.toString());
                
                // Columna 4: Total del Pedido
                PdfPCell celdaTotal = new PdfPCell(new Phrase(String.format("$ %.2f", venta.getTotal())));
                celdaTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(celdaTotal);
                
                granTotal += venta.getTotal();
            }
            
            document.add(table);

            // --- 4. FILA DE TOTAL GENERAL ---
            Paragraph pTotal = new Paragraph(String.format("TOTAL VENDIDO: $ %.2f", granTotal), fontTotal);
            pTotal.setAlignment(Element.ALIGN_RIGHT);
            pTotal.setSpacingBefore(10); // Espacio antes del total
            document.add(pTotal);

            // --- 5. CIERRE ---
            document.close(); // ¡Muy importante!

        } catch (DocumentException e) {
            e.printStackTrace();
            // (Manejo de errores)
        }
        
        return baos.toByteArray();
    }

    /**
     * Método de ayuda para crear celdas de encabezado bonitas.
     */
    private PdfPCell crearCeldaHeader(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(Color.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }
}