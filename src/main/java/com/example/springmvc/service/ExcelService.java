package com.example.springmvc.service;

import com.example.springmvc.dto.item.AssetResponse;
import com.example.springmvc.dto.item.ChemicalResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ExcelService {
    byte[] exportChemicalsToExcel(List<ChemicalResponse> chemicals);
    byte[] exportAssetsToExcel(List<AssetResponse> assets);
    List<ChemicalResponse> importChemicalsFromExcel(MultipartFile file);
    List<AssetResponse> importAssetsFromExcel(MultipartFile file);
}