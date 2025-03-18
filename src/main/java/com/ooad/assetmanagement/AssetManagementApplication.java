package com.ooad.assetmanagement;

import com.ooad.assetmanagement.javafx.AssetManagementJavaFXApplication;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AssetManagementApplication {

    public static void main(String[] args) {
        // Check if the application should run in web mode only
        boolean webMode = isWebMode(args);
        
        if (webMode) {
            // Run as a Spring Boot web application
            SpringApplication.run(AssetManagementApplication.class, args);
        } else {
            // Launch the JavaFX application
            Application.launch(AssetManagementJavaFXApplication.class, args);
        }
    }
    
    private static boolean isWebMode(String[] args) {
        for (String arg : args) {
            if (arg.equals("--web") || arg.equals("-web")) {
                return true;
            }
        }
        return false;
    }
} 