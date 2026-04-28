package utils;

import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtility {

	public static Workbook workBook;
	public static Sheet sheet;

	public static void loadExcel(String fileName, String sheetName) {
		try {
			FileInputStream fin = new FileInputStream("./src/test/resources/testData/" + fileName);
			workBook = new XSSFWorkbook(fin);
			sheet = workBook.getSheet(sheetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object[][] getSheetData(String fileName, String sheetName) {

		loadExcel(fileName, sheetName);

		int rowCount = sheet.getPhysicalNumberOfRows();
		int colCount = sheet.getRow(0).getPhysicalNumberOfCells();

		Object[][] data = new Object[rowCount - 1][colCount];

		for (int i = 1; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {

				String cellValue = sheet.getRow(i).getCell(j).toString();

				data[i - 1][j] = cellValue; // ALWAYS STRING
			}
		}
		return data;
	}

	public static String getCellData(int rowNum, int colNum) {
		Cell cell = sheet.getRow(rowNum).getCell(colNum);
		DataFormatter formatter = new DataFormatter();
		return formatter.formatCellValue(cell);
	}
}