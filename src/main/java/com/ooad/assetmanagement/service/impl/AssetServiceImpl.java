package com.ooad.assetmanagement.service.impl;

import com.ooad.assetmanagement.model.Asset;
import com.ooad.assetmanagement.repository.AssetRepository;
import com.ooad.assetmanagement.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {
    
    private final AssetRepository assetRepository;
    
    @Autowired
    public AssetServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }
    
    @Override
    public Asset createAsset(Asset asset) {
        // Set initial values if not set
        if (asset.getLastUpdated() == null) {
            asset.setLastUpdated(LocalDateTime.now());
        }
        
        if (asset.getStatus() == null) {
            asset.setStatus(Asset.AssetStatus.ACTIVE);
        }
        
        return assetRepository.save(asset);
    }
    
    @Override
    public List<Asset> findAllAssets() {
        return assetRepository.findAll();
    }
    
    @Override
    public Optional<Asset> findById(Long id) {
        return assetRepository.findById(id);
    }
    
    @Override
    public List<Asset> findByPortfolioId(Long portfolioId) {
        return assetRepository.findByPortfolioId(portfolioId);
    }
    
    @Override
    public List<Asset> findByUserId(Long userId) {
        return assetRepository.findByUserId(userId);
    }
    
    @Override
    public Asset updateAsset(Asset asset) {
        return assetRepository.save(asset);
    }
    
    @Override
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }
    
    @Override
    public void updateAllAssetValues() {
        List<Asset> assets = assetRepository.findAll();
        
        for (Asset asset : assets) {
            asset.updateCurrentValue();
            asset.setLastUpdated(LocalDateTime.now());
            assetRepository.save(asset);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Asset> List<T> findByAssetType(Class<T> assetType) {
        List<Asset> assets = assetRepository.findByAssetType(assetType);
        return assets.stream()
                .map(asset -> (T) asset)
                .collect(Collectors.toList());
    }
} 