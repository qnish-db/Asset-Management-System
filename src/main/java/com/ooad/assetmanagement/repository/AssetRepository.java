package com.ooad.assetmanagement.repository;

import com.ooad.assetmanagement.model.Asset;
import com.ooad.assetmanagement.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    
    List<Asset> findByPortfolio(Portfolio portfolio);
    
    List<Asset> findByPortfolioId(Long portfolioId);
    
    @Query("SELECT a FROM Asset a WHERE TYPE(a) = :assetType")
    List<Asset> findByAssetType(Class<?> assetType);
    
    @Query("SELECT a FROM Asset a WHERE a.portfolio.user.id = :userId")
    List<Asset> findByUserId(Long userId);
} 