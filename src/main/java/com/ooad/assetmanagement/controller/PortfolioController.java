package com.ooad.assetmanagement.controller;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/portfolios")
public class PortfolioController {
    
    private final PortfolioService portfolioService;
    private final UserService userService;
    private final AssetService assetService;
    
    @Autowired
    public PortfolioController(PortfolioService portfolioService, UserService userService, AssetService assetService) {
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.assetService = assetService;
    }
    
    @GetMapping
    public String listPortfolios(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        List<Portfolio> portfolios = portfolioService.findByUserId(user.getId());
        model.addAttribute("portfolios", portfolios);
        return "portfolios/list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("portfolio", new Portfolio());
        return "portfolios/create";
    }
    
    @PostMapping("/create")
    public String createPortfolio(@Valid @ModelAttribute("portfolio") Portfolio portfolio, BindingResult result) {
        if (result.hasErrors()) {
            return "portfolios/create";
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        portfolio.setUser(user);
        portfolioService.createPortfolio(portfolio);
        
        return "redirect:/portfolios";
    }
    
    @GetMapping("/{id}")
    public String viewPortfolio(@PathVariable Long id, Model model) {
        Portfolio portfolio = portfolioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid portfolio ID: " + id));
        
        // Check if the portfolio belongs to the current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!portfolio.getUser().getId().equals(user.getId())) {
            return "redirect:/portfolios?error=unauthorized";
        }
        
        BigDecimal totalValue = portfolioService.calculatePortfolioValue(id);
        BigDecimal totalROI = portfolioService.calculatePortfolioROI(id);
        
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("assets", assetService.findByPortfolioId(id));
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("totalROI", totalROI);
        
        return "portfolios/view";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Portfolio portfolio = portfolioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid portfolio ID: " + id));
        
        // Check if the portfolio belongs to the current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!portfolio.getUser().getId().equals(user.getId())) {
            return "redirect:/portfolios?error=unauthorized";
        }
        
        model.addAttribute("portfolio", portfolio);
        return "portfolios/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String updatePortfolio(@PathVariable Long id, @Valid @ModelAttribute("portfolio") Portfolio portfolio, BindingResult result) {
        if (result.hasErrors()) {
            return "portfolios/edit";
        }
        
        Portfolio existingPortfolio = portfolioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid portfolio ID: " + id));
        
        // Check if the portfolio belongs to the current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!existingPortfolio.getUser().getId().equals(user.getId())) {
            return "redirect:/portfolios?error=unauthorized";
        }
        
        // Update only the fields that can be changed
        existingPortfolio.setName(portfolio.getName());
        existingPortfolio.setDescription(portfolio.getDescription());
        
        portfolioService.updatePortfolio(existingPortfolio);
        
        return "redirect:/portfolios/" + id;
    }
    
    @GetMapping("/{id}/delete")
    public String deletePortfolio(@PathVariable Long id) {
        Portfolio portfolio = portfolioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid portfolio ID: " + id));
        
        // Check if the portfolio belongs to the current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!portfolio.getUser().getId().equals(user.getId())) {
            return "redirect:/portfolios?error=unauthorized";
        }
        
        portfolioService.deletePortfolio(id);
        
        return "redirect:/portfolios";
    }
} 