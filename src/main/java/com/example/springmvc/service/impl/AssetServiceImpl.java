package com.example.springmvc.service.impl;

import com.example.springmvc.dto.item.AssetResponse;
import com.example.springmvc.entity.Asset;
import com.example.springmvc.repository.AssetRepository;
import com.example.springmvc.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    @Override
    public List<AssetResponse> getAllAssets() {
        return assetRepository.findAllAssets().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssetResponse getAssetById(Integer id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài sản với ID: " + id));
        return mapToDTO(asset);
    }

    @Override
    public AssetResponse createAsset(AssetResponse dto) {
        if (assetRepository.findByItemCode(dto.getItemCode()).isPresent()) {
            throw new RuntimeException("Mã tài sản " + dto.getItemCode() + " đã tồn tại!");
        }
        Asset asset = new Asset();
        mapToEntity(dto, asset);
        // Set mặc định cho cha
        asset.setCategoryType("DEVICE");
        return mapToDTO(assetRepository.save(asset));
    }

    @Override
    public AssetResponse updateAsset(Integer id, AssetResponse dto) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài sản!"));
        mapToEntity(dto, asset);
        return mapToDTO(assetRepository.save(asset));
    }

    @Override
    public void deleteAsset(Integer id) {
        assetRepository.deleteById(id);
    }

    @Override
    public AssetResponse importOrUpdateAsset(AssetResponse dto) {
        // Tìm xem mã đã có chưa, có thì update, chưa thì tạo mới
        Asset asset = assetRepository.findByItemCode(dto.getItemCode())
                .orElse(new Asset());

        mapToEntity(dto, asset);
        if (asset.getCategoryType() == null) asset.setCategoryType("DEVICE");

        return mapToDTO(assetRepository.save(asset));
    }

    private AssetResponse mapToDTO(Asset entity) {
        AssetResponse dto = new AssetResponse();
        dto.setItemId(entity.getItemId());
        dto.setItemCode(entity.getItemCode());
        dto.setName(entity.getName());
        dto.setCategoryType(entity.getCategoryType());
        dto.setUnit(entity.getUnit());
        dto.setYearInUse(entity.getYearInUse());
        dto.setCreatedAt(entity.getCreatedAt());

        dto.setResidualValue(entity.getResidualValue());
        dto.setAccountingQuantity(entity.getAccountingQuantity());
        dto.setInventoryQuantity(entity.getInventoryQuantity());
        dto.setStatusDetail(entity.getStatusDetail());

        return dto;
    }

    private void mapToEntity(AssetResponse dto, Asset entity) {
        entity.setItemCode(dto.getItemCode());
        entity.setName(dto.getName());
        entity.setUnit(dto.getUnit());
        entity.setYearInUse(dto.getYearInUse());

        entity.setResidualValue(dto.getResidualValue());
        entity.setAccountingQuantity(dto.getAccountingQuantity());
        entity.setInventoryQuantity(dto.getInventoryQuantity());
        entity.setStatusDetail(dto.getStatusDetail());

        if(dto.getInventoryQuantity() != null) {
            entity.setCurrentQuantity(java.math.BigDecimal.valueOf(dto.getInventoryQuantity()));
        }
    }
}