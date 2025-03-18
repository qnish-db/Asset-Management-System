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
@DiscriminatorValue("MUTUAL_FUND")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MutualFund extends Asset {
    
    @NotBlank
    private String fundCode;
    
    @Positive
    private BigDecimal units;
    
    private String fundManager;
    
    private String category;
    
    private BigDecimal expenseRatio;
    
    @Override
    public void updateCurrentValue() {
        // In a real application, this would call an external API to get the current NAV
        // For now, we'll just simulate a random fluctuation
        BigDecimal currentNAV = getCurrentValue().divide(units, 2, BigDecimal.ROUND_HALF_UP);
        double randomFactor = 0.97 + Math.random() * 0.06; // Random factor between 0.97 and 1.03
        BigDecimal newNAV = currentNAV.multiply(new BigDecimal(randomFactor)).setScale(2, BigDecimal.ROUND_HALF_UP);
        setCurrentValue(newNAV.multiply(units));
        setLastUpdated(java.time.LocalDateTime.now());
    }
} 