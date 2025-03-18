package com.ooad.assetmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("REAL_ESTATE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RealEstate extends Asset {
    
    @NotBlank
    private String address;
    
    @Positive
    private Double squareFootage;
    
    private String propertyType; // Residential, Commercial, etc.
    
    private Integer yearBuilt;
    
    private BigDecimal monthlyRentalIncome;
    
    @Override
    public void updateCurrentValue() {
        // In a real application, this might use a real estate valuation API
        // For now, we'll just simulate a slow appreciation
        double randomFactor = 1.0 + (Math.random() * 0.02); // Random factor between 1.0 and 1.02
        BigDecimal newValue = getCurrentValue().multiply(new BigDecimal(randomFactor)).setScale(2, BigDecimal.ROUND_HALF_UP);
        setCurrentValue(newValue);
        setLastUpdated(java.time.LocalDateTime.now());
    }
} 