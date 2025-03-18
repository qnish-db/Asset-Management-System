package com.ooad.assetmanagement.controller;

import com.ooad.assetmanagement.model.Asset;
import com.ooad.assetmanagement.model.Portfolio;
import com.ooad.assetmanagement.model.User;
import com.ooad.assetmanagement.service.AssetService;
import com.ooad.assetmanagement.service.PortfolioService;
import com.ooad.assetmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {
    
    private final UserService userService;
    private final PortfolioService portfolioService;
    private final AssetService assetService;
    
    @Autowired
    public HomeController(UserService userService, PortfolioService portfolioService, AssetService assetService) {
        this.userService = userService;
        this.portfolioService = portfolioService;
        this.assetService = assetService;
    }
    
    @GetMapping("/")
    public String home() {
        // If user is authenticated, redirect to dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        
        return "home";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        List<Portfolio> portfolios = portfolioService.findByUserId(user.getId());
        List<Asset> recentAssets = assetService.findByUserId(user.getId());
        
        // Calculate total portfolio value
        BigDecimal totalValue = BigDecimal.ZERO;
        for (Portfolio portfolio : portfolios) {
            totalValue = totalValue.add(portfolioService.calculatePortfolioValue(portfolio.getId()));
        }
        
        model.addAttribute("user", user);
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("recentAssets", recentAssets);
        model.addAttribute("totalValue", totalValue);
        
        return "dashboard";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
} 