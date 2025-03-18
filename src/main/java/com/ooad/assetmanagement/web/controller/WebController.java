package com.ooad.assetmanagement.web.controller;

import com.ooad.assetmanagement.model.User;
import com.ooad.assetmanagement.service.AssetService;
import com.ooad.assetmanagement.service.PortfolioService;
import com.ooad.assetmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/web")
public class WebController {

    private final UserService userService;
    private final AssetService assetService;
    private final PortfolioService portfolioService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WebController(UserService userService, AssetService assetService, 
                         PortfolioService portfolioService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.assetService = assetService;
        this.portfolioService = portfolioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public String home() {
        return "redirect:/web/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        
        Optional<User> userOptional = userService.findByUsername(username);
        
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            // Store user in session
            session.setAttribute("currentUser", userOptional.get());
            return "redirect:/web/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String username,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              @RequestParam(required = false) boolean terms,
                              Model model) {
        
        // Validate input
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long");
            return "register";
        }
        
        if (!terms) {
            model.addAttribute("error", "You must agree to the terms and conditions");
            return "register";
        }
        
        // Check if username or email already exists
        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists. Please choose another one.");
            return "register";
        }
        
        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email is already registered. Please use another email or login.");
            return "register";
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        
        try {
            userService.registerUser(user);
            return "redirect:/web/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            return "redirect:/web/login";
        }
        
        // Add user data to model
        model.addAttribute("user", currentUser);
        model.addAttribute("portfolios", portfolioService.findByUserId(currentUser.getId()));
        model.addAttribute("assets", assetService.findByUserId(currentUser.getId()));
        
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/web/login";
    }
} 