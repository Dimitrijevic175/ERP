package com.dimitrijevic175.product_service.configuration;

import org.apache.poi.ss.usermodel.*;
import java.math.BigDecimal;

public class ExcelUtils {

    private static final ExcelUtils INSTANCE = new ExcelUtils();
    private ExcelUtils() {}

    public static ExcelUtils getInstance() {
        return INSTANCE;
    }

    public String getCellString(Cell cell) {
        if (cell == null) return null;

        String value = null;

        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue();
                break;

            case NUMERIC:
                double d = cell.getNumericCellValue();
                if (d == (long) d) value = String.valueOf((long) d);
                else value = String.valueOf(d);
                break;

            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING: value = cell.getStringCellValue(); break;
                    case NUMERIC:
                        double v = cell.getNumericCellValue();
                        value = (v == (long) v) ? String.valueOf((long) v) : String.valueOf(v);
                        break;
                }
                break;

            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;

            default:
                value = null;
        }

        if (value != null) {
            value = value.replace("\u00A0", "").trim();
            if (value.isEmpty()) value = null;
        }

        return value;
    }

    public Integer getCellInteger(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            }
            if (cell.getCellType() == CellType.STRING) {
                return Integer.parseInt(cell.getStringCellValue().trim());
            }
        } catch (Exception ignored) {}

        return null;
    }

    public BigDecimal getCellBigDecimal(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            }
            if (cell.getCellType() == CellType.STRING) {
                return new BigDecimal(cell.getStringCellValue().trim());
            }
        } catch (Exception ignored) {}

        return null;
    }
}
