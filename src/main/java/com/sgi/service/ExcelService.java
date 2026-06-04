package com.sgi.service;

import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

    public void generarExcelDemo() {

        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet hoja = workbook.createSheet("Demo");

            Row encabezado = hoja.createRow(0);

            encabezado.createCell(0).setCellValue("ID");
            encabezado.createCell(1).setCellValue("Incidencia");
            encabezado.createCell(2).setCellValue("Estado");

            Row fila1 = hoja.createRow(1);

            fila1.createCell(0).setCellValue(1);
            fila1.createCell(1).setCellValue("Proyector dañado");
            fila1.createCell(2).setCellValue("PENDIENTE");

            FileOutputStream archivo =
                    new FileOutputStream("demo-incidencias.xlsx");

            workbook.write(archivo);

            archivo.close();
            workbook.close();

            System.out.println("Excel generado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}