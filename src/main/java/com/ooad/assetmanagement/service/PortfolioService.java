package com.ooad.assetmanagement.service;

import com.ooad.assetmanagement.model.Portfolio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PortfolioService {
    
    Portfolio createPortfolio(Portfolio portfolio);
    
    List<Portfolio> findAllPortfolios();
    
    Optional<Portfolio> findById(Long id);
    
    List<Portfolio> findByUserId(Long userId);
    
    Portfolio updatePortfolio(Portfolio portfolio);
    
    void deletePortfolio(Long id);
    
    BigDecimal calculatePortfolioValue(Long portfolioId);
    
    BigDecimal calculatePortfolioROI(Long portfolioId);
} 