package com.ooad.assetmanagement.controller;

import com.ooad.assetmanagement.model.*;
import com.ooad.assetmanagement.service.AssetService;
import com.ooad.assetmanagement.service.PortfolioService;
import com.ooad.assetmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/assets")
public class AssetController {
    
    private final AssetService assetService;
    private final PortfolioService portfolioService;
    private final UserService userService;
    
    @Autowired
    public AssetController(AssetService assetService, PortfolioService portfolioService, UserService userService) {
        this.assetService = assetService;
        this.portfolioService = portfolioService;
        this.userService = userService;
    }
    
    @GetMapping
    public String listAssets(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        List<Asset> assets = assetService.findByUserId(user.getId());
        model.addAttribute("assets", assets);
        return "assets/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        List<Portfolio> portfolios = portfolioService.findByUserId(user.getId());
        
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("assetTypes", new String[]{"Stock", "MutualFund", "RealEstate"});
        model.addAttribute("selectedType", "Stock");
        
        return "assets/create";
    }
    
    @PostMapping("/create/stock")
    public String createStock(@Valid @ModelAttribute("stock") Stock stock, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            
            model.addAttribute("portfolios", portfolioService.findByUserId(user.getId()));
            model.addAttribute("assetTypes", new String[]{"Stock", "MutualFund", "RealEstate"});
            model.addAttribute("selectedType", "Stock");
            
            return "assets/create";
        }
        
        assetService.createAsset(stock);
        return "redirect:/portfolios/" + stock.getPortfolio().getId();
    }
    
    @PostMapping("/create/mutualfund")
    public String createMutualFund(@Valid @ModelAttribute("mutualFund") MutualFund mutualFund, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            
            model.addAttribute("portfolios", portfolioService.findByUserId(user.getId()));
            model.addAttribute("assetTypes", new String[]{"Stock", "MutualFund", "RealEstate"});
            model.addAttribute("selectedType", "MutualFund");
            
            return "assets/create";
        }
        
        assetService.createAsset(mutualFund);
        return "redirect:/portfolios/" + mutualFund.getPortfolio().getId();
    }
    
    @PostMapping("/create/realestate")
    public String createRealEstate(@Valid @ModelAttribute("realEstate") RealEstate realEstate, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            
            model.addAttribute("portfolios", portfolioService.findByUserId(user.getId()));
            model.addAttribute("assetTypes", new String[]{"Stock", "MutualFund", "RealEstate"});
            model.addAttribute("selectedType", "RealEstate");
            
            return "assets/create";
        }
        
        assetService.createAsset(realEstate);
        return "redirect:/portfolios/" + realEstate.getPortfolio().getId();
    }
    
    @GetMapping("/{id}")
    public String viewAsset(@PathVariable Long id, Model model) {
        Asset asset = assetService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid asset ID: " + id));
        
        // Check if the asset belongs to the current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!asset.getPortfolio().getUser().getId().equals(user.getId())) {
            return "redirect:/assets?error=unauthorized";
        }
        
        model.addAttribute("asset", asset);
        
        // Determine the asset type and add specific attributes
        if (asset instanceof Stock) {
            model.addAttribute("assetType", "Stock");
            model.addAttribute("stock", (Stock) asset);
        } else if (asset instanceof MutualFund) {
            model.addAttribute("assetType", "MutualFund");
            model.addAttribute("mutualFund", (MutualFund) asset);
        } else if (asset instanceof RealEstate) {
            model.addAttribute("assetType", "RealEstate");
            model.addAttribute("realEstate", (RealEstate) asset);
        }
        
        return "assets/view";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Asset asset = assetService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid asset ID: " + id));
        
        // Check if the asset belongs to the current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!asset.getPortfolio().getUser().getId().equals(user.getId())) {
            return "redirect:/assets?error=unauthorized";
        }
        
        model.addAttribute("asset", asset);
        model.addAttribute("portfolios", portfolioService.findByUserId(user.getId()));
        
        // Determine the asset type and add specific attributes
        if (asset instanceof Stock) {
            model.addAttribute("assetType", "Stock");
            model.addAttribute("stock", (Stock) asset);
            return "assets/edit-stock";
        } else if (asset instanceof MutualFund) {
            model.addAttribute("assetType", "MutualFund");
            model.addAttribute("mutualFund", (MutualFund) asset);
            return "assets/edit-mutualfund";
        } else if (asset instanceof RealEstate) {
            model.addAttribute("assetType", "RealEstate");
            model.addAttribute("realEstate", (RealEstate) asset);
            return "assets/edit-realestate";
        }
        
        return "redirect:/assets";
    }
    
    @PostMapping("/{id}/edit/stock")
    public String updateStock(@PathVariable Long id, @Valid @ModelAttribute("stock") Stock stock, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            
            model.addAttribute("portfolios", portfolioService.findByUserId(user.getId()));
            model.addAttribute("assetType", "Stock");
            
            return "assets/edit-stock";
        }
        
        assetService.updateAsset(stock);
        return "redirect:/assets/" + id;
    }
    
    @PostMapping("/{id}/edit/mutualfund")
    public String updateMutualFund(@PathVariable Long id, @Valid @ModelAttribute("mutualFund") MutualFund mutualFund, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            
            model.addAttribute("portfolios", portfolioService.findByUserId(user.getId()));
            model.addAttribute("assetType", "MutualFund");
            
            return "assets/edit-mutualfund";
        }
        
        assetService.updateAsset(mutualFund);
        return "redirect:/assets/" + id;
    }
    
    @PostMapping("/{id}/edit/realestate")
    public String updateRealEstate(@PathVariable Long id, @Valid @ModelAttribute("realEstate") RealEstate realEstate, BindingResult result, Model model) {
        if (result.hasErrors()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUsername(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            
            model.addAttribute("portfolios", portfolioService.findByUserId(user.getId()));
            model.addAttribute("assetType", "RealEstate");
            
            return "assets/edit-realestate";
        }
        
        assetService.updateAsset(realEstate);
        return "redirect:/assets/" + id;
    }
    
    @GetMapping("/{id}/delete")
    public String deleteAsset(@PathVariable Long id) {
        Asset asset = assetService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid asset ID: " + id));
        
        // Check if the asset belongs to the current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!asset.getPortfolio().getUser().getId().equals(user.getId())) {
            return "redirect:/assets?error=unauthorized";
        }
        
        Long portfolioId = asset.getPortfolio().getId();
        assetService.deleteAsset(id);
        
        return "redirect:/portfolios/" + portfolioId;
    }
    
    @GetMapping("/update-values")
    public String updateAllAssetValues() {
        assetService.updateAllAssetValues();
        return "redirect:/assets?updated";
    }
} 