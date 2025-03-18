package com.ooad.assetmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "asset_type", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    @Positive
    private BigDecimal purchasePrice;
    
    @NotNull
    @Positive
    private BigDecimal currentValue;
    
    @NotNull
    private LocalDateTime purchaseDate;
    
    private LocalDateTime lastUpdated;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
    
    @Enumerated(EnumType.STRING)
    private AssetStatus status;
    
    public enum AssetStatus {
        ACTIVE,
        SOLD,
        PENDING
    }
    
    // Method to calculate return on investment
    public BigDecimal calculateROI() {
        if (purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentValue.subtract(purchasePrice)
                .divide(purchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
    
    // Method to update current value
    public abstract void updateCurrentValue();
} 