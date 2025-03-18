package com.ooad.assetmanagement.controller;

import com.ooad.assetmanagement.model.User;
import com.ooad.assetmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        // Check if username or email already exists
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username is already taken");
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email is already in use");
        }
        
        if (result.hasErrors()) {
            return "register";
        }
        
        userService.registerUser(user);
        return "redirect:/login?registered";
    }
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    
    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        model.addAttribute("user", user);
        return "profile";
    }
    
    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        model.addAttribute("user", user);
        return "edit-profile";
    }
    
    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "edit-profile";
        }
        
        userService.updateUser(user);
        return "redirect:/profile?updated";
    }
} 