package com.zapacciano.sgr.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;

/**
 * Servicio dedicado a crear el archivo PDF en memoria.
 */
@Service
public class PdfService {

    /**
     * Este método crea el reporte de ventas en PDF.
     * Por ahora, usa datos de ejemplo.
     * Más adelante, le puedes pasar una lista de 'Ventas'
     * desde tu base de datos.
     */
    public byte[] generarReporteVentas() {
        
        // 1. Crear un 'flujo' de bytes en memoria.
        // Aquí es donde el PDF se escribirá temporalmente.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 2. Crear el Documento PDF
        // El 'PageSize.A4' define el tamaño de la hoja.
        Document document = new Document(PageSize.A4);

        try {
            // 3. Asociar el Documento con el 'flujo' de salida
            PdfWriter.getInstance(document, baos);

            // 4. Abrir el documento para empezar a escribir
            document.open();

            // --- AÑADIR CONTENIDO AL PDF ---

            // 5. Título
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph titulo = new Paragraph("Reporte de Ventas - Zapacciano", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20); // Espacio después del título
            document.add(titulo);

            // 6. Párrafo introductorio
            document.add(new Paragraph("Este es un resumen de las ventas generadas. (Datos de ejemplo)"));
            document.add(Chunk.NEWLINE); // Línea en blanco

            // 7. Crear la Tabla de datos
            // Una tabla con 3 columnas
            PdfPTable tabla = new PdfPTable(3); 
            tabla.setWidthPercentage(100); // Que ocupe todo el ancho
            
            // Encabezados de la tabla
            tabla.addCell("Producto");
            tabla.addCell("Cantidad Vendida");
            tabla.addCell("Total Recaudado");

            // --- DATOS DE EJEMPLO ---
            // (Más adelante, esto vendría de un bucle `for(Venta venta : ventas)`)
            tabla.addCell("Pizza Margarita");
            tabla.addCell("30");
            tabla.addCell("$ 45000.00");

            tabla.addCell("Hamburguesa Clásica");
            tabla.addCell("50");
            tabla.addCell("$ 60000.00");

            tabla.addCell("Ensalada César");
            tabla.addCell("20");
            tabla.addCell("$ 28000.00");
            
            // Total
            tabla.addCell("TOTAL");
            tabla.addCell("100");
            tabla.addCell("$ 133000.00");
            // --- FIN DATOS DE EJEMPLO ---

            document.add(tabla);

            // --- FIN DEL CONTENIDO ---

            // 8. Cerrar el documento
            document.close();

        } catch (DocumentException e) {
            // Manejo básico de errores (en la vida real, loggear esto)
            e.printStackTrace();
        }

        // 9. Devolver los bytes del PDF
        return baos.toByteArray();
    }
}