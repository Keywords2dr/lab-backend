package com.example.springmvc.service;

import com.example.springmvc.dto.item.ChemicalResponse;
import java.util.List;

public interface ChemicalService {
    List<ChemicalResponse> getAllChemicals();
    List<ChemicalResponse> getLowStockChemicals();
    ChemicalResponse getChemicalById(Integer id);
    ChemicalResponse createChemical(ChemicalResponse chemicalDTO);
    ChemicalResponse updateChemical(Integer id, ChemicalResponse chemicalDTO);
    void deleteChemical(Integer id);
    ChemicalResponse importOrUpdateChemical(ChemicalResponse chemicalDTO);
}