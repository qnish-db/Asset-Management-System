package com.ooad.assetmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "portfolios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Asset> assets = new HashSet<>();
    
    // Helper methods to maintain bidirectional relationship
    public void addAsset(Asset asset) {
        assets.add(asset);
        asset.setPortfolio(this);
    }
    
    public void removeAsset(Asset asset) {
        assets.remove(asset);
        asset.setPortfolio(null);
    }
} 