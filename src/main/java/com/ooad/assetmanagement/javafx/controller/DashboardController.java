package com.ooad.assetmanagement.javafx.controller;

import com.ooad.assetmanagement.model.Asset;
import com.ooad.assetmanagement.model.MutualFund;
import com.ooad.assetmanagement.model.Portfolio;
import com.ooad.assetmanagement.model.RealEstate;
import com.ooad.assetmanagement.model.Stock;
import com.ooad.assetmanagement.model.User;
import com.ooad.assetmanagement.service.AssetService;
import com.ooad.assetmanagement.service.PortfolioService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DashboardController {

    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Label totalValueLabel;
    
    @FXML
    private Label totalReturnLabel;
    
    @FXML
    private Label portfolioCountLabel;
    
    @FXML
    private Label assetCountLabel;
    
    @FXML
    private PieChart assetAllocationChart;
    
    @FXML
    private LineChart<String, Number> performanceChart;
    
    @FXML
    private TableView<Asset> topAssetsTable;
    
    @FXML
    private TableColumn<Asset, String> assetNameColumn;
    
    @FXML
    private TableColumn<Asset, String> assetTypeColumn;
    
    @FXML
    private TableColumn<Asset, String> assetValueColumn;
    
    @FXML
    private TableColumn<Asset, Number> assetROIColumn;
    
    @FXML
    private TableView<ActivityEntry> recentActivitiesTable;
    
    @FXML
    private TableColumn<ActivityEntry, String> activityDateColumn;
    
    @FXML
    private TableColumn<ActivityEntry, String> activityTypeColumn;
    
    @FXML
    private TableColumn<ActivityEntry, String> activityDescriptionColumn;
    
    @FXML
    private TableColumn<ActivityEntry, String> activityValueColumn;
    
    private final AssetService assetService;
    private final PortfolioService portfolioService;
    private final ApplicationContext applicationContext;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private final NumberFormat percentFormat = NumberFormat.getPercentInstance();
    
    private User currentUser;
    
    @Autowired
    public DashboardController(AssetService assetService, PortfolioService portfolioService, ApplicationContext applicationContext) {
        this.assetService = assetService;
        this.portfolioService = portfolioService;
        this.applicationContext = applicationContext;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Load data after user is set
        loadData();
    }
    
    @FXML
    public void initialize() {
        // Configure table columns
        assetNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        assetTypeColumn.setCellValueFactory(cellData -> {
            Asset asset = cellData.getValue();
            if (asset instanceof Stock) {
                return new SimpleStringProperty("Stock");
            } else if (asset instanceof MutualFund) {
                return new SimpleStringProperty("Mutual Fund");
            } else if (asset instanceof RealEstate) {
                return new SimpleStringProperty("Real Estate");
            } else {
                return new SimpleStringProperty("Other");
            }
        });
        assetValueColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(currencyFormat.format(cellData.getValue().getCurrentValue())));
        assetROIColumn.setCellValueFactory(cellData -> {
            BigDecimal roi = cellData.getValue().calculateROI();
            return new SimpleDoubleProperty(roi.doubleValue());
        });
        
        // Activity table setup
        activityDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDate()));
        activityTypeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getType()));
        activityDescriptionColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDescription()));
        activityValueColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getValue()));
        
        // Initialize charts
        initializePieChart();
        initializeLineChart();
    }
    
    /**
     * Loads data when the dashboard is first displayed
     */
    public void loadData() {
        if (currentUser == null) {
            return;
        }
        
        // Set welcome message
        welcomeLabel.setText("Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName());
        
        // Get user's portfolios
        List<Portfolio> portfolios = portfolioService.findByUserId(currentUser.getId());
        
        // Get all assets
        List<Asset> assets = assetService.findByUserId(currentUser.getId());
        
        // Update summary statistics
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        
        for (Asset asset : assets) {
            totalValue = totalValue.add(asset.getCurrentValue());
            totalCost = totalCost.add(asset.getPurchasePrice());
        }
        
        totalValueLabel.setText(currencyFormat.format(totalValue));
        
        BigDecimal totalReturn = totalValue.subtract(totalCost);
        totalReturnLabel.setText(currencyFormat.format(totalReturn));
        if (totalReturn.compareTo(BigDecimal.ZERO) >= 0) {
            totalReturnLabel.getStyleClass().add("value-positive");
        } else {
            totalReturnLabel.getStyleClass().add("value-negative");
        }
        
        portfolioCountLabel.setText(String.valueOf(portfolios.size()));
        assetCountLabel.setText(String.valueOf(assets.size()));
        
        // Update asset allocation chart
        updateAssetAllocationChart(assets);
        
        // Update performance chart
        updatePerformanceChart(portfolios);
        
        // Update top assets table
        updateTopAssetsTable(assets);
        
        // Update recent activities
        updateRecentActivitiesTable(assets);
    }
    
    @FXML
    void handleRefresh(ActionEvent event) {
        // Update asset values
        assetService.updateAllAssetValues();
        
        // Reload the data
        loadData();
    }
    
    @FXML
    void handleDashboardNav(MouseEvent event) {
        // Already on dashboard, no action needed
    }
    
    @FXML
    void handlePortfoliosNav(MouseEvent event) {
        loadView("/fxml/Portfolios.fxml", "Asset Management System - Portfolios");
    }
    
    @FXML
    void handleAssetsNav(MouseEvent event) {
        loadView("/fxml/Assets.fxml", "Asset Management System - Assets");
    }
    
    @FXML
    void handleReportsNav(MouseEvent event) {
        loadView("/fxml/Reports.fxml", "Asset Management System - Reports");
    }
    
    @FXML
    void handleSettingsNav(MouseEvent event) {
        loadView("/fxml/Settings.fxml", "Asset Management System - Settings");
    }
    
    @FXML
    void handleLogout(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            loader.setControllerFactory(controllerClass -> applicationContext.getBean(controllerClass));
            Parent loginRoot = loader.load();
            
            Scene scene = new Scene(loginRoot, 800, 600);
            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Asset Management System - Login");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerClass -> {
                Object controller = applicationContext.getBean(controllerClass);
                if (controller instanceof UserAwareController) {
                    ((UserAwareController) controller).setCurrentUser(currentUser);
                }
                return controller;
            });
            
            Parent root = loader.load();
            Scene scene = new Scene(root, 1024, 768);
            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle(title);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializePieChart() {
        assetAllocationChart.setTitle("Asset Allocation");
        assetAllocationChart.setLabelsVisible(true);
        assetAllocationChart.setLegendVisible(true);
    }
    
    private void updateAssetAllocationChart(List<Asset> assets) {
        Map<String, BigDecimal> assetTypeValues = new HashMap<>();
        
        // Calculate total value for each asset type
        for (Asset asset : assets) {
            String assetType;
            if (asset instanceof Stock) {
                assetType = "Stocks";
            } else if (asset instanceof MutualFund) {
                assetType = "Mutual Funds";
            } else if (asset instanceof RealEstate) {
                assetType = "Real Estate";
            } else {
                assetType = "Other";
            }
            
            BigDecimal currentValue = asset.getCurrentValue();
            assetTypeValues.put(assetType, assetTypeValues.getOrDefault(assetType, BigDecimal.ZERO).add(currentValue));
        }
        
        // Create chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, BigDecimal> entry : assetTypeValues.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + 
                             " (" + currencyFormat.format(entry.getValue()) + ")", entry.getValue().doubleValue()));
        }
        
        assetAllocationChart.setData(pieChartData);
    }
    
    private void initializeLineChart() {
        performanceChart.setTitle("Portfolio Performance");
        performanceChart.setAnimated(false);
    }
    
    private void updatePerformanceChart(List<Portfolio> portfolios) {
        performanceChart.getData().clear();
        
        for (Portfolio portfolio : portfolios) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(portfolio.getName());
            
            // In a real application, we would get historical data for each portfolio
            // For this demo, we'll just create some dummy data points
            
            // Start with current value
            BigDecimal currentValue = portfolioService.calculatePortfolioValue(portfolio.getId());
            
            // Generate 6 data points, one for each month
            for (int i = 6; i >= 0; i--) {
                LocalDateTime date = LocalDateTime.now().minusMonths(i);
                String month = date.format(DateTimeFormatter.ofPattern("MMM"));
                
                // Add some random variation (between -5% and +10%)
                double factor = 1.0 - (i * 0.03) + (Math.random() * 0.02);
                BigDecimal value = currentValue.multiply(BigDecimal.valueOf(factor));
                
                series.getData().add(new XYChart.Data<>(month, value.doubleValue()));
            }
            
            performanceChart.getData().add(series);
        }
    }
    
    private void updateTopAssetsTable(List<Asset> assets) {
        // Sort assets by ROI in descending order
        List<Asset> topAssets = assets.stream()
                .sorted(Comparator.comparing(Asset::calculateROI).reversed())
                .limit(5)
                .collect(Collectors.toList());
        
        // Add to table
        topAssetsTable.setItems(FXCollections.observableArrayList(topAssets));
    }
    
    private void updateRecentActivitiesTable(List<Asset> assets) {
        // In a real application, we would get actual transaction history
        // For this demo, we'll just create some dummy activity entries based on assets
        
        ObservableList<ActivityEntry> activities = FXCollections.observableArrayList();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Add purchase activities for all assets
        for (Asset asset : assets) {
            ActivityEntry entry = new ActivityEntry(
                asset.getPurchaseDate().format(formatter),
                "Purchase",
                "Purchased " + asset.getName(),
                currencyFormat.format(asset.getPurchasePrice())
            );
            activities.add(entry);
        }
        
        // Add some random update activities
        for (int i = 0; i < Math.min(3, assets.size()); i++) {
            Asset asset = assets.get(i);
            ActivityEntry entry = new ActivityEntry(
                LocalDateTime.now().minusDays(i).format(formatter),
                "Update",
                "Updated value of " + asset.getName(),
                currencyFormat.format(asset.getCurrentValue())
            );
            activities.add(entry);
        }
        
        // Sort by date (most recent first)
        activities.sort(Comparator.comparing(ActivityEntry::getDate).reversed());
        
        // Limit to 10 activities
        if (activities.size() > 10) {
            activities = FXCollections.observableArrayList(activities.subList(0, 10));
        }
        
        recentActivitiesTable.setItems(activities);
    }
    
    // Inner class for activity entries
    public static class ActivityEntry {
        private final String date;
        private final String type;
        private final String description;
        private final String value;
        
        public ActivityEntry(String date, String type, String description, String value) {
            this.date = date;
            this.type = type;
            this.description = description;
            this.value = value;
        }
        
        public String getDate() {
            return date;
        }
        
        public String getType() {
            return type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    // Marker interface for controllers that need current user
    public interface UserAwareController {
        void setCurrentUser(User user);
    }
} 