package com.sgi.service;

import com.sgi.model.Incidencia;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarReporteIncidencias(List<Incidencia> incidencias) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte SGI");

            // Crear fuentes y estilos para la cabecera
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            // Estilos para datos
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);

            // Cabeceras del reporte
            String[] columns = {
                "ID Incidencia", "Tipo Incidencia", "Categoría", "Prioridad", "Ubicación", 
                "Docente Reportante", "Estado", "Fecha Creación", "Fecha Cierre", "Técnico Asignado", "Resolución / Motivo"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            for (Incidencia inc : incidencias) {
                Row row = sheet.createRow(rowIdx++);

                createCell(row, 0, inc.getIdIncidencia() != null ? String.valueOf(inc.getIdIncidencia()) : "", dataCellStyle);
                createCell(row, 1, inc.getTipoIncidencia() != null ? inc.getTipoIncidencia() : "", dataCellStyle);
                createCell(row, 2, inc.getCategoria() != null ? inc.getCategoria() : "", dataCellStyle);
                createCell(row, 3, inc.getPrioridad() != null ? inc.getPrioridad() : "", dataCellStyle);
                createCell(row, 4, inc.getUbicacion() != null ? inc.getUbicacion() : "", dataCellStyle);
                
                String docenteNombre = inc.getUsuario() != null 
                    ? inc.getUsuario().getNombres() + " " + inc.getUsuario().getApellidos() 
                    : "No especificado";
                createCell(row, 5, docenteNombre, dataCellStyle);
                createCell(row, 6, inc.getEstado() != null ? inc.getEstado() : "", dataCellStyle);

                String fechaCreacionStr = inc.getFechaCreacion() != null ? inc.getFechaCreacion().format(DATE_FORMATTER) : "";
                createCell(row, 7, fechaCreacionStr, dataCellStyle);

                String fechaCierreStr = inc.getFechaCierre() != null ? inc.getFechaCierre().format(DATE_FORMATTER) : "";
                createCell(row, 8, fechaCierreStr, dataCellStyle);

                createCell(row, 9, inc.getAsignadoA() != null ? inc.getAsignadoA() : "Sin asignar", dataCellStyle);
                createCell(row, 10, inc.getResolucion() != null ? inc.getResolucion() : "", dataCellStyle);
            }

            // Auto-ajustar tamaño de las columnas para mejor lectura
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
