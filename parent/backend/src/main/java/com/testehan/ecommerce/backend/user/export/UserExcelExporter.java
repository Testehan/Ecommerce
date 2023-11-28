package com.testehan.ecommerce.backend.user.export;

import com.testehan.ecommerce.backend.user.export.AbstractExporter;
import com.testehan.ecommerce.common.entity.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.util.List;

public class UserExcelExporter extends AbstractExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public UserExcelExporter() {
        this.workbook =  new XSSFWorkbook();
        this.sheet = workbook.createSheet("Users");
    }

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        setupResponseHeader(response,"xlsx","application/octet-stream");

        writeHeader();
        writeDataLines(listUsers);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }

    private void writeHeader(){
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        cellStyle.setFont(font);

        createCell(row, 0, "User ID", cellStyle);
        createCell(row, 1, "Email", cellStyle);
        createCell(row, 2, "First Name", cellStyle);
        createCell(row, 3, "Last Name", cellStyle);
        createCell(row, 4, "Roles", cellStyle);
        createCell(row, 5, "Enabled", cellStyle);
    }

    private void writeDataLines(List<User> listUsers) {
        int rowIndex = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (User user : listUsers) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;

            createCell(row, columnIndex++, user.getId(), cellStyle);
            createCell(row, columnIndex++, user.getEmail(), cellStyle);
            createCell(row, columnIndex++, user.getFirstName(), cellStyle);
            createCell(row, columnIndex++, user.getLastName(), cellStyle);
            createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
            createCell(row, columnIndex++, user.isEnabled(), cellStyle);
        }
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle cellStyle){
        XSSFCell cell = row.createCell(columnIndex);

        if (value instanceof Integer){
            cell.setCellValue((Integer)value);
        } else if (value instanceof Boolean){
            cell.setCellValue((Boolean)value);
        } else {
            cell.setCellValue((String)value);
        }

        cell.setCellStyle(cellStyle);
    }
}
