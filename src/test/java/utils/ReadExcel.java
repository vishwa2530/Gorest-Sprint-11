package utils;
import java.io.FileInputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ReadExcel {
    public static void main(String[] args) throws Exception {
        FileInputStream fin = new FileInputStream("D:\\Sprint\\sprint\\src\\test\\resources\\testData\\testData.xlsx");
        Workbook workBook = new XSSFWorkbook(fin);
        Sheet sheet = workBook.getSheet("Sheet1");
        int rowCount = sheet.getPhysicalNumberOfRows();
        System.out.println("Total Rows: " + rowCount);
        for(int i=0; i<sheet.getLastRowNum()+1; i++) {
            Row row = sheet.getRow(i);
            if(row == null) {
                System.out.println("Row " + i + " is null");
                continue;
            }
            System.out.print("Row " + i + ": ");
            for(int j=0; j<row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                System.out.print((cell == null ? "null" : cell.toString()) + " | ");
            }
            System.out.println();
        }
    }
}
