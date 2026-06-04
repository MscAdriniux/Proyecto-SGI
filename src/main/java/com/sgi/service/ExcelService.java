package com.sgi.service;

import com.sgi.model.Incidencia;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

    public byte[] generarReporte(List<Incidencia> incidencias) {

        try (
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {

            Sheet hoja = workbook.createSheet("Incidencias");

            Row encabezado = hoja.createRow(0);

            encabezado.createCell(0).setCellValue("ID");
            encabezado.createCell(1).setCellValue("Tipo");
            encabezado.createCell(2).setCellValue("Categoria");
            encabezado.createCell(3).setCellValue("Prioridad");
            encabezado.createCell(4).setCellValue("Estado");
            encabezado.createCell(5).setCellValue("Ubicacion");

            int fila = 1;

            for (Incidencia i : incidencias) {

                Row row = hoja.createRow(fila++);

                row.createCell(0).setCellValue(i.getIdIncidencia());
                row.createCell(1).setCellValue(i.getTipoIncidencia());
                row.createCell(2).setCellValue(i.getCategoria());
                row.createCell(3).setCellValue(i.getPrioridad());
                row.createCell(4).setCellValue(i.getEstado());
                row.createCell(5).setCellValue(i.getUbicacion());
            }

            for (int c = 0; c < 6; c++) {
                hoja.autoSizeColumn(c);
            }

            workbook.write(output);

            return output.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }
}
