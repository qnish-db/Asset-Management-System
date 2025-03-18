package com.ooad.assetmanagement.service;

import com.ooad.assetmanagement.model.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetService {
    
    Asset createAsset(Asset asset);
    
    List<Asset> findAllAssets();
    
    Optional<Asset> findById(Long id);
    
    List<Asset> findByPortfolioId(Long portfolioId);
    
    List<Asset> findByUserId(Long userId);
    
    Asset updateAsset(Asset asset);
    
    void deleteAsset(Long id);
    
    void updateAllAssetValues();
    
    <T extends Asset> List<T> findByAssetType(Class<T> assetType);
} 