package com.ooad.assetmanagement.service.impl;

import com.ooad.assetmanagement.model.Asset;
import com.ooad.assetmanagement.model.Portfolio;
import com.ooad.assetmanagement.repository.AssetRepository;
import com.ooad.assetmanagement.repository.PortfolioRepository;
import com.ooad.assetmanagement.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    
    @Autowired
    public PortfolioServiceImpl(PortfolioRepository portfolioRepository, AssetRepository assetRepository) {
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
    }
    
    @Override
    public Portfolio createPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }
    
    @Override
    public List<Portfolio> findAllPortfolios() {
        return portfolioRepository.findAll();
    }
    
    @Override
    public Optional<Portfolio> findById(Long id) {
        return portfolioRepository.findById(id);
    }
    
    @Override
    public List<Portfolio> findByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }
    
    @Override
    public Portfolio updatePortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }
    
    @Override
    public void deletePortfolio(Long id) {
        portfolioRepository.deleteById(id);
    }
    
    @Override
    public BigDecimal calculatePortfolioValue(Long portfolioId) {
        List<Asset> assets = assetRepository.findByPortfolioId(portfolioId);
        
        return assets.stream()
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public BigDecimal calculatePortfolioROI(Long portfolioId) {
        List<Asset> assets = assetRepository.findByPortfolioId(portfolioId);
        
        if (assets.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalInvestment = assets.stream()
                .map(Asset::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCurrentValue = assets.stream()
                .map(Asset::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalInvestment.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalCurrentValue.subtract(totalInvestment)
                .divide(totalInvestment, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
} 