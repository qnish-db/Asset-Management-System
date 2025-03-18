package com.ooad.assetmanagement.javafx.controller;

import com.ooad.assetmanagement.model.User;
import com.ooad.assetmanagement.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class LoginController {

    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Label errorMessageLabel;
    
    private final UserService userService;
    private final ApplicationContext applicationContext;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public LoginController(UserService userService, ApplicationContext applicationContext, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.applicationContext = applicationContext;
        this.passwordEncoder = passwordEncoder;
    }
    
    @FXML
    public void initialize() {
        // Clear any error messages
        errorMessageLabel.setText("");
        
        // Disable login button when fields are empty
        loginButton.disableProperty().bind(
            usernameField.textProperty().isEmpty()
            .or(passwordField.textProperty().isEmpty())
        );
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Check for empty fields (additional validation)
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password cannot be empty");
            return;
        }
        
        // Authenticate user
        Optional<User> userOptional = userService.findByUsername(username);
        
        if (!userOptional.isPresent() || !passwordEncoder.matches(password, userOptional.get().getPassword())) {
            showError("Invalid username or password");
            return;
        }
        
        // Authentication successful
        try {
            // Load the dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            
            Parent dashboardRoot = loader.load();
            
            // Now that the FXML is loaded, get the controller and set the user
            DashboardController controller = loader.getController();
            controller.setCurrentUser(userOptional.get());
            
            Scene scene = new Scene(dashboardRoot, 1024, 768);
            
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Asset Management System - Dashboard");
            currentStage.centerOnScreen();
            
        } catch (IOException e) {
            showError("Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
            loader.setControllerFactory(controllerClass -> applicationContext.getBean(controllerClass));
            Parent registerRoot = loader.load();
            
            Scene scene = new Scene(registerRoot, 800, 600);
            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Asset Management System - Register");
            
        } catch (IOException e) {
            showError("Failed to load registration page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Displays an error message to the user
     */
    private void showError(String message) {
        errorMessageLabel.setText(message);
    }
} 