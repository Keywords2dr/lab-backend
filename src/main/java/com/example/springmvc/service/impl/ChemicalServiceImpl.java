package com.example.springmvc.service.impl;

import com.example.springmvc.dto.item.ChemicalResponse;
import com.example.springmvc.entity.Chemical;
import com.example.springmvc.repository.ChemicalRepository;
import com.example.springmvc.service.ChemicalService;
import com.example.springmvc.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChemicalServiceImpl implements ChemicalService {

    private final ChemicalRepository chemicalRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChemicalResponse> getAllChemicals() {
        return chemicalRepository.findAllChemicals().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChemicalResponse> getLowStockChemicals() {
        return chemicalRepository.findLowStockChemicals(new BigDecimal("10")).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChemicalResponse getChemicalById(Integer id) {
        Chemical chemical = chemicalRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hóa chất với ID: " + id));
        return mapToDTO(chemical);
    }

    @Override
    public ChemicalResponse createChemical(ChemicalResponse dto) {
        if (chemicalRepository.findByItemCode(dto.getItemCode()).isPresent()) {
            throw new BusinessException("Mã hóa chất '" + dto.getItemCode() + "' đã tồn tại!");
        }
        Chemical chemical = new Chemical();
        mapToEntity(dto, chemical);
        chemical.setCategoryType("CHEMICAL");
        return mapToDTO(chemicalRepository.save(chemical));
    }

    @Override
    public ChemicalResponse updateChemical(Integer id, ChemicalResponse dto) {
        Chemical chemical = chemicalRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy thông tin hóa chất!"));

        chemicalRepository.findByItemCode(dto.getItemCode()).ifPresent(existing -> {
            if (!existing.getItemId().equals(id)) {
                throw new BusinessException("Mã hóa chất mới đã được sử dụng bởi một hóa chất khác!");
            }
        });

        mapToEntity(dto, chemical);
        return mapToDTO(chemicalRepository.save(chemical));
    }

    @Override
    public void deleteChemical(Integer id) {
        if (!chemicalRepository.existsById(id)) {
            throw new BusinessException("Hóa chất không tồn tại hoặc đã bị xóa trước đó.");
        }
        try {
            chemicalRepository.deleteById(id);
            chemicalRepository.flush();
        } catch (Exception e) {
            throw new BusinessException("Không thể xóa! Hóa chất này đang nằm trong lịch sử mượn trả.");
        }
    }

    @Override
    public ChemicalResponse importOrUpdateChemical(ChemicalResponse dto) {
        Chemical chemical = chemicalRepository.findByItemCode(dto.getItemCode())
                .orElse(new Chemical());

        if (chemical.getItemId() != null) {
            BigDecimal current = chemical.getCurrentQuantity() != null ? chemical.getCurrentQuantity() : BigDecimal.ZERO;
            BigDecimal incoming = dto.getCurrentQuantity() != null ? dto.getCurrentQuantity() : BigDecimal.ZERO;
            chemical.setCurrentQuantity(current.add(incoming));
            chemical.setName(dto.getName());
        } else {
            mapToEntity(dto, chemical);
            chemical.setCategoryType("CHEMICAL");
        }
        return mapToDTO(chemicalRepository.save(chemical));
    }

    private ChemicalResponse mapToDTO(Chemical entity) {
        ChemicalResponse dto = new ChemicalResponse();
        dto.setItemId(entity.getItemId());
        dto.setItemCode(entity.getItemCode());
        dto.setName(entity.getName());
        dto.setCategoryType(entity.getCategoryType());
        dto.setUnit(entity.getUnit());
        dto.setCurrentQuantity(entity.getCurrentQuantity());
        dto.setLockedQuantity(entity.getLockedQuantity() != null ? entity.getLockedQuantity() : BigDecimal.ZERO);
        dto.setYearInUse(entity.getYearInUse());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setFormula(entity.getFormula());
        dto.setPackaging(entity.getPackaging());
        dto.setSupplier(entity.getSupplier());
        dto.setStorageLocation(entity.getStorageLocation());
        dto.setOriginalPrice(entity.getOriginalPrice());
        return dto;
    }

    private void mapToEntity(ChemicalResponse dto, Chemical entity) {
        entity.setItemCode(dto.getItemCode());
        entity.setName(dto.getName());
        entity.setUnit(dto.getUnit());
        entity.setCurrentQuantity(dto.getCurrentQuantity() != null ? dto.getCurrentQuantity() : BigDecimal.ZERO);
        entity.setOriginalPrice(dto.getOriginalPrice() != null ? dto.getOriginalPrice() : BigDecimal.ZERO);
        entity.setYearInUse(dto.getYearInUse());
        entity.setFormula(dto.getFormula());
        entity.setPackaging(dto.getPackaging());
        entity.setSupplier(dto.getSupplier());
        entity.setStorageLocation(dto.getStorageLocation());
    }
}