package com.example.springmvc.service;

import com.example.springmvc.dto.item.AssetResponse;
import java.util.List;

public interface AssetService {
    List<AssetResponse> getAllAssets();
    AssetResponse getAssetById(Integer id);
    AssetResponse createAsset(AssetResponse assetDTO);
    AssetResponse updateAsset(Integer id, AssetResponse assetDTO);
    void deleteAsset(Integer id);
    AssetResponse importOrUpdateAsset(AssetResponse assetDTO);
}