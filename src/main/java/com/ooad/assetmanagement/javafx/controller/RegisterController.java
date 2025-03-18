package com.ooad.assetmanagement.javafx.controller;

import com.ooad.assetmanagement.model.User;
import com.ooad.assetmanagement.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RegisterController {

    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField lastNameField;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private CheckBox termsCheckBox;
    
    @FXML
    private Text errorMessage;
    
    private final UserService userService;
    private final ApplicationContext applicationContext;
    
    @Autowired
    public RegisterController(UserService userService, ApplicationContext applicationContext) {
        this.userService = userService;
        this.applicationContext = applicationContext;
    }
    
    @FXML
    void handleRegister(ActionEvent event) {
        // Clear any previous error messages
        errorMessage.setVisible(false);
        errorMessage.setManaged(false);
        
        // Validate input fields
        if (!validateInputs()) {
            return;
        }
        
        // Check if username or email already exists
        if (userService.existsByUsername(usernameField.getText().trim())) {
            showError("Username already exists. Please choose another one.");
            return;
        }
        
        if (userService.existsByEmail(emailField.getText().trim())) {
            showError("Email is already registered. Please use another email or login.");
            return;
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(firstNameField.getText().trim());
        user.setLastName(lastNameField.getText().trim());
        user.setUsername(usernameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setPassword(passwordField.getText());
        
        try {
            userService.registerUser(user);
            
            // Redirect to login page
            handleLoginLink(event);
            
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    void handleCancel(ActionEvent event) {
        handleLoginLink(event);
    }
    
    @FXML
    void handleLoginLink(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            loader.setControllerFactory(controllerClass -> applicationContext.getBean(controllerClass));
            Parent loginRoot = loader.load();
            Scene scene = new Scene(loginRoot, 800, 600);
            
            Stage currentStage = (Stage) firstNameField.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Asset Management System - Login");
            
        } catch (IOException e) {
            showError("Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateInputs() {
        // Check for empty fields
        if (firstNameField.getText().trim().isEmpty()) {
            showError("First name is required");
            return false;
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            showError("Last name is required");
            return false;
        }
        
        if (usernameField.getText().trim().isEmpty()) {
            showError("Username is required");
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showError("Email is required");
            return false;
        }
        
        // Validate email format
        String email = emailField.getText().trim();
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showError("Invalid email format");
            return false;
        }
        
        // Check password
        if (passwordField.getText().isEmpty()) {
            showError("Password is required");
            return false;
        }
        
        if (passwordField.getText().length() < 6) {
            showError("Password must be at least 6 characters long");
            return false;
        }
        
        // Check if passwords match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return false;
        }
        
        // Check terms agreement
        if (!termsCheckBox.isSelected()) {
            showError("You must agree to the terms and conditions");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
        errorMessage.setManaged(true);
    }
} 