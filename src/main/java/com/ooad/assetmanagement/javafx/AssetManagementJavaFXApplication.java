package com.ooad.assetmanagement.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetManagementJavaFXApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(AssetManagementJavaFXApplication.class);
    private ConfigurableApplicationContext springContext;
    private Parent rootNode;
    private static final String APPLICATION_TITLE = "Asset Management System";

    public static void main(String[] args) {
        logger.info("Starting JavaFX application");
        launch(args);
    }

    @Override
    public void init() throws Exception {
        logger.info("Initializing JavaFX application");
        // Initialize Spring context without web server
        springContext = new SpringApplicationBuilder(com.ooad.assetmanagement.AssetManagementApplication.class)
                .web(WebApplicationType.NONE) // Disable web server for JavaFX mode
                .run();
        
        logger.info("Loading FXML resources");
        try {
            // Check if images directory exists, create if needed
            java.io.File imagesDir = new java.io.File("src/main/resources/images");
            if (!imagesDir.exists()) {
                logger.info("Creating images directory");
                imagesDir.mkdirs();
            }
            
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            rootNode = fxmlLoader.load();
            logger.info("FXML resources loaded successfully");
        } catch (Exception e) {
            logger.error("Error loading FXML resources", e);
            throw e;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting JavaFX UI");
        // Configure and show the stage
        primaryStage.setTitle(APPLICATION_TITLE);
        Scene scene = new Scene(rootNode, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        logger.info("JavaFX UI displayed");
    }

    @Override
    public void stop() {
        logger.info("Stopping JavaFX application");
        // Clean up Spring context
        springContext.close();
    }
} 