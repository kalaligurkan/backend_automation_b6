package apache_poi_excel;

import utils.ExcelUtil;

import java.util.List;

public class TestingExcelUtil {

    public static void main(String[] args) {


        /**
         * Open the Excel file and define which sheet we want to work on.
         */
        ExcelUtil.openExcelFile("ReadData", "Sheet1");

        List<String> columnValues = ExcelUtil.getColumnValues(2);
        System.out.println("Values from the 2nd column: " + columnValues);

        String singleCell = ExcelUtil.getValue(1, 2);

        System.out.println(singleCell);

        List<String> rowValues = ExcelUtil.getRowValues(2);
        System.out.println("Values from the 3rd row: " + rowValues);
    }
}