package com.example.dbsph4.gui;

import com.example.dbsph4.database.SessionManager;
import com.example.dbsph4.models.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;

public class MainController {

    private BorderPane mainLayout;
    private StackPane contentArea;
    private VBox sidebar;
    private HBox topBar;
    private Label lblUserStatus;
    private Button btnAuthAction; // Dynamic button for Login/Logout

    public MainController() {
        System.out.println("[GUI-TRACE] Constructing Public Storefront Dashboard...");
        mainLayout = new BorderPane();
        contentArea = new StackPane();
        sidebar = new VBox(15);
        topBar = new HBox(20);

        buildBaseLayout();
        updateDynamicUIBasedOnRole();
    }

    /**
     * @return The complete root layout pane to be attached to the Scene in Main.java
     */
    public BorderPane getView() {
        return mainLayout;
    }

    /**
     * Builds the static skeleton of the application (Top Bar and Content Panel)
     */
    private void buildBaseLayout() {
        // 1. Setup Top Header Bar
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: #1abc9c;"); // Fresh e-commerce look
        topBar.setAlignment(Pos.CENTER_RIGHT);

        Label logoLabel = new Label("CoreTech Store");
        logoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Push logo to the left, auth controls to the right
        HBox logoContainer = new HBox(logoLabel);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(logoContainer, javafx.scene.layout.Priority.ALWAYS);

        lblUserStatus = new Label("Browsing as: Guest");
        lblUserStatus.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");

        btnAuthAction = new Button("Login / Sign Up");
        btnAuthAction.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-cursor: hand;");
        btnAuthAction.setOnAction(e -> triggerLoginWindow());

        topBar.getChildren().addAll(logoContainer, lblUserStatus, btnAuthAction);

        // 2. Setup Sidebar Skeleton
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50; -fx-min-width: 220px;");
        sidebar.setAlignment(Pos.TOP_CENTER);

        // 3. Central Content Area
        contentArea.setStyle("-fx-background-color: #f5f6fa; -fx-padding: 20px;");

        // Assemble base layout
        mainLayout.setTop(topBar);
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(contentArea);
    }

    /**
     * CRITICAL LOGIC: Updates navigation items dynamically depending on the current Session Role
     */
    public void updateDynamicUIBasedOnRole() {
        sidebar.getChildren().clear();
        contentArea.getChildren().clear();

        // Title for navigation block
        Label menuTitle = new Label("NAVIGATION MENU");
        menuTitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");
        sidebar.getChildren().add(menuTitle);

        // Standard Buttons visible to everyone
        Button btnProducts = createNavButton("🛒 Browse Products");
        Button btnCategories = createNavButton("📂 View Categories");

        // Connect to the actual dynamic database-backed controllers
        btnProducts.setOnAction(e -> contentArea.getChildren().setAll(new ProductController().getView()));
        btnCategories.setOnAction(e -> contentArea.getChildren().setAll(new CategoryController().getView()));

        sidebar.getChildren().addAll(btnProducts, btnCategories);

        // Role Specific Rendering
        if (SessionManager.isGuest()) {
            lblUserStatus.setText("Browsing as: Guest");
            btnAuthAction.setText("Login / Sign Up");

            // Protected features show restriction warnings for Guest
            Button btnCart = createNavButton("🔒 My Shopping Cart");
            Button btnOrders = createNavButton("🔒 Order History");

            btnCart.setOnAction(e -> showGuestRestrictionAlert("View your shopping cart"));
            btnOrders.setOnAction(e -> showGuestRestrictionAlert("View orders and receipts"));

            sidebar.getChildren().addAll(btnCart, btnOrders);
            showDefaultCatalogPlaceholder();

        } else if (SessionManager.isCustomer()) {
            lblUserStatus.setText("Welcome, " + SessionManager.getCurrentCustomer().getFirstName());
            btnAuthAction.setText("Logout");

            // Unlocked active features for customer
            Button btnCart = createNavButton("🛍️ My Shopping Cart");
            Button btnOrders = createNavButton("📄 My Orders");

            btnCart.setOnAction(e -> contentArea.getChildren().setAll(new TransactionController().getView()));
            btnOrders.setOnAction(e -> contentArea.getChildren().setAll(new Label("Active Customer Invoices Loading (Transactions table)...")));

            sidebar.getChildren().addAll(btnCart, btnOrders);
            showDefaultCatalogPlaceholder();

        } else if (SessionManager.isOwner()) {
            lblUserStatus.setText("Role: STORE OWNER (Admin)");
            btnAuthAction.setText("Logout");

            // Complete Backend Control panel for administrative duties
            Button btnManageWarehouses = createNavButton("🏭 Manage Warehouses");
            Button btnGlobalTransactions = createNavButton("📊 Global Sales Reports");
            Button btnCustomerList = createNavButton("👥 Registered Customers");

            // Connect UI Components to dynamic controllers and DAOs
            btnManageWarehouses.setOnAction(e -> contentArea.getChildren().setAll(new WarehouseController().getView()));

            // Refactored Customer List to retrieve data seamlessly using CustomerDAO
            btnCustomerList.setOnAction(e -> {
                TableView<Customer> table = new TableView<>();

                TableColumn<Customer, Integer> cId = new TableColumn<>("ID");
                cId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("customerId"));
                cId.setPrefWidth(60);

                TableColumn<Customer, String> cName = new TableColumn<>("Full Name");
                cName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getFirstName() + " " + cell.getValue().getLastName()));
                cName.setPrefWidth(140);

                TableColumn<Customer, String> cEmail = new TableColumn<>("Email");
                cEmail.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
                cEmail.setPrefWidth(180);

                TableColumn<Customer, String> cPhone = new TableColumn<>("Phone");
                cPhone.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));
                cPhone.setPrefWidth(120);

                table.getColumns().addAll(cId, cName, cEmail, cPhone);
                table.setItems(com.example.dbsph4.dao.CustomerDAO.getAllCustomers()); // Synchronized via DAO

                VBox box = new VBox(10, new Label("👥 Registered Customer Records System"), table);
                box.setPadding(new Insets(20));
                contentArea.getChildren().setAll(box);
            });

            btnGlobalTransactions.setOnAction(e -> {
                VBox box = new VBox(15);
                box.setPadding(new Insets(25));
                box.setAlignment(Pos.TOP_LEFT);

                Label lblTitle = new Label("📊 Administrative Business Intelligence Analytics (20 Queries System)");
                lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                box.getChildren().add(lblTitle);

                // Dynamic Display for QUERY 5: Current Month Online Revenue (Enforcing 2026 Context)
                double currentMonthSales = com.example.dbsph4.dao.TransactionDAO.getCurrentMonthRevenue();
                Label lblQ5 = new Label("📈 [Query 5] Platform Revenue (Current Month 2026): $" + String.format("%.2f", currentMonthSales));
                lblQ5.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

                // Dynamic Display for QUERY 9: Average items per online bill transaction
                double avgItems = com.example.dbsph4.dao.TransactionDAO.getAverageItemsPerTransaction();
                Label lblQ9 = new Label("🛒 [Query 9] Average Items Purchased Per Order: " + String.format("%.2f", avgItems) + " units");
                lblQ9.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");

                // Dynamic Display for QUERY 19: Registered Laptop Warranties Active Status count
                int activeLaptopWarranties = com.example.dbsph4.dao.TransactionDAO.getActiveWarrantiesForLaptops();
                Label lblQ19 = new Label("🛡️ [Query 19] Total Active Laptop Warranties: " + activeLaptopWarranties + " registered");
                lblQ19.setStyle("-fx-font-size: 13px; -fx-text-fill: #2980b9;");

                box.getChildren().addAll(lblQ5, lblQ9, lblQ19);

                // Add sub-list placeholder explaining background traces for remaining queries
                Label lblInfo = new Label("💡 Note: Remaining targeting queries (Queries 1,2,3,4,6,7,8,10,11,12,13,14,15,17,18,20) " +
                        "are wired into active row events and execute live console output telemetry upon user clicks.");
                lblInfo.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-padding: 10 0 0 0;");
                box.getChildren().add(lblInfo);

                contentArea.getChildren().setAll(box);
            });

            sidebar.getChildren().addAll(btnManageWarehouses, btnGlobalTransactions, btnCustomerList);

            // Administrative opening screen
            VBox adminBox = new VBox(10);
            adminBox.setAlignment(Pos.CENTER);
            Label lblAdmin = new Label("Secure Administrative Central Control Active");
            lblAdmin.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #c0392b;");
            adminBox.getChildren().add(lblAdmin);
            contentArea.getChildren().add(adminBox);
        }
    }

    /**
     * Helper to style navigation buttons consistently
     */
    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 10px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 10px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 10px;"));
        return btn;
    }

    private void showGuestRestrictionAlert(String operation) {
        contentArea.getChildren().clear();
        VBox alertBox = new VBox(15);
        alertBox.setAlignment(Pos.CENTER);
        Label msg = new Label("Access Denied: Action Requires Authentication");
        msg.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        Label subMsg = new Label("To " + operation + ", you must log into a registered account.");
        subMsg.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
        alertBox.getChildren().addAll(msg, subMsg);
        contentArea.getChildren().add(alertBox);
    }

    private void showDefaultCatalogPlaceholder() {
        contentArea.getChildren().clear();
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        Label title = new Label("🛍️ CoreTech Public Showcase Catalog 🛍️");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label sub = new Label("All visitors can browse items. Click 'Browse Products' or sign in to access your custom cart.");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
        box.getChildren().addAll(title, sub);
        contentArea.getChildren().add(box);
    }

    /**
     * Logic routing to authentication module
     */
    private void triggerLoginWindow() {
        if (!SessionManager.isGuest()) {
            SessionManager.logout();
            updateDynamicUIBasedOnRole();
        } else {
            System.out.println("[GUI-TRACE] Injecting Login Module interface into content panel.");
            contentArea.getChildren().setAll(new LoginController(this).getView());
        }
    }
}