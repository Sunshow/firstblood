package com.bjgl.web.utils;

import com.bjgl.web.bean.PairValue;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.*;

public class ExcelUtil {
    protected final static Logger logger = LoggerFactory.getLogger(ExcelUtil.class.getName());

    protected static ThreadLocal<InputStream> excelInputLocal = new ThreadLocal<InputStream>();

    public static Workbook createWorkbook(File excelFile) {
        closeExcel();

        try {
            Workbook rwb = null;

            try {
                InputStream excelInput = new FileInputStream(excelFile);
                excelInputLocal.set(excelInput);

                rwb = new HSSFWorkbook(excelInput);
            } catch (Exception ex) {
                try {
                    closeExcel();
                    InputStream excelInput = new FileInputStream(excelFile);
                    excelInputLocal.set(excelInput);
                    rwb = new XSSFWorkbook(excelInput);
                } catch (Exception ex1) {
                    logger.error("转换excel文件格式不正确", ex1);
                }
            }

            if (rwb == null) {
                closeExcel();
            }

            return rwb;
        } catch (Exception e) {
            logger.error("打开流文件出错", e);
            closeExcel();
        }

        return null;
    }

    /**
     * 查找出现对应列的行和索引顺序
     * @param columnNames 要查找的列名
     * @return 对应列所在的行和索引顺序，未找到返回null
     */
    public static PairValue<Integer, int[]> findRowIndexByColumnName(Sheet sheet, String[] columnNames) {
        Set<String> findColumnSet = new HashSet<String>();
        for (String columnName : columnNames) {
            findColumnSet.add(columnName);
        }
        // 遍历所有单元格
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row =  rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Map<String, Integer> currentFindColumnMap = new HashMap<String, Integer>();
            while (cellIterator.hasNext()) {
                Cell cell =  cellIterator.next();
                String columnName = getCellValue(cell);
                if (columnName == null) {
                    continue;
                }
                columnName = columnName.trim();
                if (findColumnSet.contains(columnName)) {
                    currentFindColumnMap.put(columnName, cell.getColumnIndex());
                }
            }
            if (currentFindColumnMap.keySet().size() == findColumnSet.size()) {
                int[] columnIndex = new int[findColumnSet.size()];
                for (int i = 0; i < columnNames.length; i++) {
                    columnIndex[i] = currentFindColumnMap.get(columnNames[i]);
                }
                return new PairValue<Integer, int[]>(row.getRowNum(), columnIndex);
            }
        }

        return null;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        } else {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    NumberFormat formater = NumberFormat.getInstance();
                    formater.setGroupingUsed(false);
                    cellValue = formater.format(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = Boolean.valueOf(cell.getBooleanCellValue()).toString();
                    break;
                case Cell.CELL_TYPE_ERROR:
                    cellValue = String.valueOf(cell.getErrorCellValue());
                    break;
                default:
                    cellValue = "";
            }
        }
        return cellValue.trim();

    }

    /**
     * 关闭Excel文件
     */
    public static void closeExcel() {
        try {
            if (excelInputLocal.get() != null) {
                excelInputLocal.get().close();
                excelInputLocal.remove();
            }
        } catch (IOException e) {
            logger.error("流文件关闭异常 ，{}", e);
        }
    }
}
