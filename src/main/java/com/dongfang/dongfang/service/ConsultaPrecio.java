package com.dongfang.dongfang.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ConsultaPrecio {

    private String filePath;

    public ConsultaPrecio(String filePath) {
        this.filePath = filePath;
    }

    public String obtenerValorPorCantidadYColumnas(int cantidad, String tamanio, String color, String tipo) {
        try (InputStream input = new FileInputStream(new File(filePath));
             XSSFWorkbook libro = new XSSFWorkbook(input)) {

            XSSFSheet hoja = libro.getSheet("Volantes");

            // Limitar a las primeras 50 filas
            int maxFila = Math.min(50, hoja.getLastRowNum());
            for (int rowNum = 0; rowNum <= maxFila; rowNum++) {
                Row fila = hoja.getRow(rowNum);
                if (fila == null) continue;

                Cell celdaCantidad = fila.getCell(0);
                int valorCantidad = limpiarValorNumerico(celdaCantidad);
                if (valorCantidad != cantidad) continue;

                // Buscar segunda columna que cumpla los criterios
                Row filaTamano = hoja.getRow(1);
                Row filaColor = hoja.getRow(2);
                Row filaTipo = hoja.getRow(3);
                if (filaTamano == null || filaColor == null || filaTipo == null) continue;

                int lastColumn = filaTamano.getLastCellNum();
                int matchCount = 0;

                for (int col = 1; col < lastColumn; col++) {
                    String s = obtenerTexto(filaTamano.getCell(col));
                    String c = obtenerTexto(filaColor.getCell(col));
                    String t = obtenerTexto(filaTipo.getCell(col));

                    if (s.equalsIgnoreCase(tamanio) && c.equalsIgnoreCase(color) && t.equalsIgnoreCase(tipo)) {
                        matchCount++;
                        if (matchCount == 2) {
                            Cell valorCell = fila.getCell(col);
                            return obtenerTexto(valorCell);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // No se encontró
    }

    private int limpiarValorNumerico(Cell cell) {
        if (cell == null) return -1;
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    String str = cell.getStringCellValue().replaceAll("[^0-9]", "");
                    return Integer.parseInt(str);
                case FORMULA:
                    return (int) cell.getNumericCellValue();
                default:
                    return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    private String obtenerTexto(Cell cell) {
        if (cell == null) return "";
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    return String.valueOf((int) cell.getNumericCellValue());
                case FORMULA:
                    FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    switch (cellValue.getCellType()) {
                        case NUMERIC:
                            return String.valueOf((int) cellValue.getNumberValue());
                        case STRING:
                            return cellValue.getStringValue().trim();
                        default:
                            return "";
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case BLANK:
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }


    public static void main(String[] args) {
        ConsultaPrecio consulta = new ConsultaPrecio("D:\\descargas\\PRECIOS ARRIBENOS 250819.xlsx");

        String resultado = consulta.obtenerValorPorCantidadYColumnas(2000, "20x28", "BI COLOR", "OBRA");
        System.out.println("Valor final: " + resultado);
    }
}
