package com.example.springmvc.service.impl;

import com.example.springmvc.dto.item.AssetResponse;
import com.example.springmvc.dto.item.ChemicalResponse;
import com.example.springmvc.service.ExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Override
    public byte[] exportChemicalsToExcel(List<ChemicalResponse> chemicals) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Hóa chất");
            Row header = sheet.createRow(0);
            String[] headers = {"Mã", "Tên", "Công thức", "Quy cách", "Đơn vị", "Số lượng", "NCC"};

            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (ChemicalResponse c : chemicals) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getItemCode());
                row.createCell(1).setCellValue(c.getName());
                row.createCell(2).setCellValue(c.getFormula());
                row.createCell(3).setCellValue(c.getPackaging());
                row.createCell(4).setCellValue(c.getUnit());
                row.createCell(5).setCellValue(c.getCurrentQuantity() != null ? c.getCurrentQuantity().doubleValue() : 0);
                row.createCell(6).setCellValue(c.getSupplier());
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi export Excel: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportAssetsToExcel(List<AssetResponse> assets) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Thiết bị");
            Row header = sheet.createRow(0);
            String[] headers = {"Mã", "Tên", "Đơn vị", "SL Sổ sách", "SL Kiểm kê", "Tình trạng"};

            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (AssetResponse a : assets) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(a.getItemCode());
                row.createCell(1).setCellValue(a.getName());
                row.createCell(2).setCellValue(a.getUnit());
                row.createCell(3).setCellValue(a.getAccountingQuantity() != null ? a.getAccountingQuantity() : 0);
                row.createCell(4).setCellValue(a.getInventoryQuantity() != null ? a.getInventoryQuantity() : 0);
                row.createCell(5).setCellValue(a.getStatusDetail());
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi export Excel");
        }
    }

    @Override
    public List<ChemicalResponse> importChemicalsFromExcel(MultipartFile file) {
        return Collections.emptyList();
    }

    @Override
    public List<AssetResponse> importAssetsFromExcel(MultipartFile file) {
        return Collections.emptyList();
    }
}