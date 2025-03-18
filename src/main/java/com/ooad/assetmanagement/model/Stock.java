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
@DiscriminatorValue("STOCK")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Stock extends Asset {
    
    @NotBlank
    private String ticker;
    
    @Positive
    private Integer quantity;
    
    private String exchange;
    
    private String sector;
    
    private BigDecimal dividendYield;
    
    @Override
    public void updateCurrentValue() {
        // In a real application, this would call an external API to get the current stock price
        // For now, we'll just simulate a random fluctuation
        BigDecimal currentPrice = getCurrentValue().divide(new BigDecimal(quantity), 2, BigDecimal.ROUND_HALF_UP);
        double randomFactor = 0.95 + Math.random() * 0.1; // Random factor between 0.95 and 1.05
        BigDecimal newPrice = currentPrice.multiply(new BigDecimal(randomFactor)).setScale(2, BigDecimal.ROUND_HALF_UP);
        setCurrentValue(newPrice.multiply(new BigDecimal(quantity)));
        setLastUpdated(java.time.LocalDateTime.now());
    }
} 