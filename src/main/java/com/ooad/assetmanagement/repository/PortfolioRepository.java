package com.ooad.assetmanagement.repository;

import com.ooad.assetmanagement.model.Portfolio;
import com.ooad.assetmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    List<Portfolio> findByUser(User user);
    
    List<Portfolio> findByUserId(Long userId);
} 