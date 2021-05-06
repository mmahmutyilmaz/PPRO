package com.ppro.utilities;
import org.apache.poi.ss.usermodel.*;
import org.testng.Assert;
import java.io.FileInputStream;

public class ExcelUtil {

    private Sheet workSheet;
    private Workbook workBook;
    private String path;

    public ExcelUtil(String path, String sheetName) {
        this.path = path;
        try {
            // Open the Excel file
            FileInputStream ExcelFile = new FileInputStream(path);
            // Access the required test data sheet
            workBook = WorkbookFactory.create(ExcelFile);
            workSheet = workBook.getSheet(sheetName);
            // check if sheet is null or not. null means  sheetname was wrong

            Assert.assertNotNull(workSheet, "Sheet: \""+sheetName+"\" does not exist\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String[][] getDataArrayWithoutFirstRow() {
        DataFormatter formatter = new DataFormatter();

        String[][] data = new String[rowCount()-1][columnCount()];

        for (int i = 1; i < rowCount(); i++) {
            for (int j = 0; j < columnCount(); j++) {
                String value = formatter.formatCellValue(workSheet.getRow(i).getCell(j));
                data[i-1][j] = value;
            }
        }
        return data;

    }

    public int columnCount() {
        return workSheet.getRow(0).getLastCellNum();
    }

    public int rowCount() {
        return workSheet.getLastRowNum()+1;
    }

}