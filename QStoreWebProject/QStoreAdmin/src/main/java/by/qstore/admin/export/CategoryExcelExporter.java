package by.qstore.admin.export;

import by.qstore.common.entity.Category;
import by.qstore.common.entity.User;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CategoryExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public CategoryExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String fileTimestamp = dateTimeFormatter.format(LocalDateTime.now());
        String fileName = "categories_" + fileTimestamp + ".xlsx";

        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        writeTableHeader();
        writeData(categoryList);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);

        workbook.close();
        outputStream.close();
    }

    private void writeData(List<Category> categoryList) {
        int rowIndex = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (Category category : categoryList) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;

            createCell(row, columnIndex++, category.getId(), cellStyle);
            createCell(row, columnIndex++, category.getName(), cellStyle);
        }
    }

    private void writeTableHeader() {
        sheet = workbook.createSheet("Categories");
        XSSFRow headerRow = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(headerRow, 0, "Category ID", cellStyle);
        createCell(headerRow, 1, "Name", cellStyle);
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else {
            cell.setCellValue((String) value);
        }

        cell.setCellStyle(style);
    }
}
